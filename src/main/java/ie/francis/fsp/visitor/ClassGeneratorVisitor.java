/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import static ie.francis.fsp.ast.NodeType.*;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fsp.ast.*;
import ie.francis.fsp.environment.*;
import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.runtime.type.Function;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class ClassGeneratorVisitor implements Visitor {

  private final String className;
  private final Environment environment;
  private final ClassWriter cw;
  private MethodVisitor mv;

  private int quoteDepth = 0;
  private final Map<String, Integer> vars;

  private LocalVariablesSorter lvs;

  public ClassGeneratorVisitor(String className, List<String> parameters, Environment environment) {

    this.className = className;
    this.environment = environment;
    this.vars = new HashMap<>();

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        Opcodes.ACC_PUBLIC + ACC_MODULE,
        String.format("%s", className),
        null,
        "java/lang/Object",
        new String[] {});

    String descriptor =
        "(" + "Ljava/lang/Object;".repeat(parameters.size()) + ")Ljava/lang/Object;";

    for (int i = 0; i < parameters.size(); i++) {
      vars.put(parameters.get(i), i);
    }

    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", descriptor, null, null);
    lvs = new LocalVariablesSorter(0, "()Ljava/lang/Object;", mv);
    mv.visitCode();
  }

  @Override
  public void visit(ProgramNode programNode) {
    for (Node node : programNode.getNodes()) {
      node.accept(this);
    }
  }

  @Override
  public void visit(SxprNode sxprNode) {
    sxprNode.getCar().accept(this);
    sxprNode.getCdr().accept(this);
  }

  @Override
  public void visit(ListNode listNode) {
    List<Node> nodes = listNode.getNodes();

    if (this.quoteDepth > 0) {
      compileQuotedList(nodes);
      return;
    }

    Node first = nodes.remove(0);
    if (isSpecialForm(first)) {
      compileSpecialForm(first.value(), nodes);
    } else if (isFunction(first)) {
      ie.francis.fsp.runtime.type.Type symbol = environment.get(first.value());
      if (isVariadic(symbol)) {
        compileArrayOfArguments(nodes);
      } else {
        compileArguments(nodes);
      }
      compileFunctionCall(symbol);
    }
  }

  private void compileQuotedList(List<Node> nodes) {

    mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fsp/runtime/helper/ConsBuilder");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKESPECIAL, "ie/francis/fsp/runtime/helper/ConsBuilder", "<init>", "()V", false);

    int id = lvs.newLocal(Type.getType("Lie/francis/fsp/runtime/helper/ConsBuilder"));
    mv.visitVarInsn(ASTORE, id);

    for (Node node : nodes) {
      mv.visitVarInsn(ALOAD, id);
      node.accept(this);
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/fsp/runtime/helper/ConsBuilder",
          "add",
          "(Ljava/lang/Object;)V",
          false);
    }
    mv.visitVarInsn(ALOAD, id);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "ie/francis/fsp/runtime/helper/ConsBuilder",
        "getCons",
        "()Lie/francis/fsp/runtime/type/Cons;",
        false);
  }

  private void compileSpecialForm(String value, List<Node> nodes) {
    switch (value) {
      case "quote":
        compileQuoteSpecialForm(nodes);
        break;
      case "func":
        compileFuncSpecialForm(nodes);
        break;
      case "if":
        compileIfSpecialForm(nodes);
        break;
      case "progn":
        compilePrognSpecialForm(nodes);
        break;
    }
  }

  private void compileQuoteSpecialForm(List<Node> nodes) {
    this.quoteDepth++;
    for (Node node : nodes) {
      node.accept(this);
    }
    this.quoteDepth--;
  }

  private void compileFuncSpecialForm(List<Node> nodes) {

    String functionName = nodes.get(0).value();
    String className = functionName.substring(0, 1).toUpperCase() + functionName.substring(1);

    List<String> args = new ArrayList<>();
    List<Node> parameters = ((ListNode) nodes.get(1)).getNodes();
    for (Node value : parameters) {
      args.add(value.value());
    }

    String descriptor =
        "(" + "Ljava/lang/Object;".repeat(parameters.size()) + ")Ljava/lang/Object;";

    environment.put(functionName, new Function(className + ".run", descriptor));

    ClassGeneratorVisitor cgv = new ClassGeneratorVisitor(className, args, environment);
    Node code = nodes.get(2);
    code.accept(cgv);
    cgv.write();

    environment.loadClass(className, cgv.generate());
    mv.visitInsn(ACONST_NULL);
  }

  private void compileIfSpecialForm(List<Node> nodes) {

    // Compile condition, expecting a ListNode
    nodes.get(0).accept(this);

    mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
    Label startOfElseLabel = new Label();
    Label endOfIfLabel = new Label();
    // Compare result of condition
    mv.visitJumpInsn(IFEQ, startOfElseLabel);

    // Compile true block
    nodes.get(1).accept(this);

    // Jump past the false block after entering the true block if it exists
    mv.visitJumpInsn(GOTO, endOfIfLabel);
    mv.visitLabel(startOfElseLabel);

    // Compile false block if it exists
    if (nodes.size() > 2) {
      nodes.get(2).accept(this);
    } else {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitLabel(endOfIfLabel);
  }

  private void compilePrognSpecialForm(List<Node> nodes) {}

  private void compileFunctionCall(ie.francis.fsp.runtime.type.Type symbol) {
    String owner = symbol.name().split("\\.")[0];
    String functionName = symbol.name().split("\\.")[1];
    mv.visitMethodInsn(INVOKESTATIC, owner, functionName, symbol.descriptor(), false);
  }

  private void compileArguments(List<Node> nodes) {
    for (Node node : nodes) {
      node.accept(this);
    }
  }

  private void compileArrayOfArguments(List<Node> nodes) {
    // Specify array size first
    mv.visitLdcInsn(nodes.size());
    // Create a new array of Objects
    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
    mv.visitInsn(DUP);

    for (int i = 0; i < nodes.size(); i++) {

      mv.visitLdcInsn(i);
      nodes.get(i).accept(this);
      mv.visitInsn(AASTORE);

      if (i < nodes.size() - 1) {
        mv.visitInsn(DUP);
      }
    }
  }

  private boolean isSpecialForm(Node first) {
    String value = first.value();
    if (first.type() == SYMBOL_NODE) {
      switch (value) {
        case "quote":
        case "func":
        case "if":
        case "progn":
          return true;
      }
    }
    return false;
  }

  private boolean isFunction(Node first) {
    String value = first.value();
    if (first.type() == SYMBOL_NODE) {
      if (environment.contains(value)) {
        ie.francis.fsp.runtime.type.Type symbol = environment.get(value);
        return symbol.type() == DataType.FUNCTION;
      }
    }
    return false;
  }

  private boolean isVariadic(ie.francis.fsp.runtime.type.Type function) {
    return function.descriptor().startsWith("([");
  }

  @Override
  public void visit(NumberNode numberNode) {
    mv.visitLdcInsn(numberNode.getIntValue());
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
  }

  @Override
  public void visit(StringNode stringNode) {
    mv.visitLdcInsn(stringNode.value());
  }

  @Override
  public void visit(SymbolNode symbolNode) {
    if (quoteDepth > 0) {
      mv.visitLdcInsn(symbolNode.value());
      return;
    }
    if (vars.containsKey(symbolNode.value())) {
      lvs.visitVarInsn(ALOAD, vars.get(symbolNode.value()));
    }
  }

  @Override
  public void visit(BooleanNode booleanNode) {
    mv.visitLdcInsn(booleanNode.value());
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Boolean",
        "valueOf",
        "(Ljava/lang/String;)Ljava/lang/Boolean;",
        false);
  }

  public byte[] generate() {
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
    return cw.toByteArray();
  }

  public void write() {
    try (FileOutputStream fos = new FileOutputStream(this.className + ".class")) {
      fos.write(this.generate());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getClassName() {
    return className;
  }
}
