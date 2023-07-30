/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import static ie.francis.fsp.ast.NodeType.*;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fsp.ast.*;
import ie.francis.fsp.sym.FunctionSymbol;
import ie.francis.fsp.sym.Symbol;
import ie.francis.fsp.sym.SymbolTable;
import ie.francis.fsp.sym.SymbolType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class ClassGeneratorVisitor implements Visitor {

  private final SymbolTable symbolTable;
  private final ClassWriter cw;
  private final MethodVisitor mv;

  private int quoteDepth = 0;
  private final Map<String, Integer> vars;

  private final LocalVariablesSorter lvs;

  public ClassGeneratorVisitor(String className) {

    vars = new HashMap<>();
    symbolTable = new SymbolTable();
    symbolTable.put(
        "+",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.plus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "-",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.minus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "*",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.multiply",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "/",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.divide",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "<",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.lessThan",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        ">",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.greaterThan",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "=",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.equals",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "concat",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.concat",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "read",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.read",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "car",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.car",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "cdr",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.cdr",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "println",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.println",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        Opcodes.ACC_PUBLIC + ACC_MODULE,
        String.format("ie/francis/%s", className),
        null,
        "java/lang/Object",
        new String[] {});

    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", "()Ljava/lang/Object;", null, null);
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
      Symbol symbol = symbolTable.get(first.value());
      if (isVariadic(symbol)) {
        compileArrayOfArguments(nodes);
      } else {
        compileArguments(nodes);
      }
      compileFunctionCall(symbol);
    }
  }

  private void compileQuotedList(List<Node> nodes) {

    mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fsp/runtime/type/LispList");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKESPECIAL, "ie/francis/fsp/runtime/type/LispList", "<init>", "()V", false);

    int id = lvs.newLocal(Type.getType("Ljava/util/List"));
    mv.visitVarInsn(ASTORE, id);

    for (Node node : nodes) {
      mv.visitVarInsn(ALOAD, id);
      node.accept(this);
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/fsp/runtime/type/LispList",
          "add",
          "(Ljava/lang/Object;)Z",
          false);
      mv.visitInsn(POP);
    }
    mv.visitVarInsn(ALOAD, id);
  }

  private void compileSpecialForm(String value, List<Node> nodes) {
    switch (value) {
      case "quote":
        compileQuoteSpecialForm(nodes);
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
    //
    // Compile false block if it exists
    if (nodes.size() > 2) {
      nodes.get(2).accept(this);
    } else {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitLabel(endOfIfLabel);
  }

  private void compilePrognSpecialForm(List<Node> nodes) {}

  private void compileFunctionCall(Symbol symbol) {
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
      if (symbolTable.contains(value)) {
        Symbol symbol = symbolTable.get(value);
        return symbol.type() == SymbolType.FUNCTION;
      }
    }
    return false;
  }

  private boolean isVariadic(Symbol function) {
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
    mv.visitLdcInsn(symbolNode.value());
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
}
