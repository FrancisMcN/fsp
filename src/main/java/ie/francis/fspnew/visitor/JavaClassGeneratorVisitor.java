/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.visitor;

import static ie.francis.fspnew.node.NodeType.LAMBDA_NODE;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fspnew.compiler.Artifact;
import ie.francis.fspnew.node.*;
import ie.francis.fspnew.repl.Environment;
import java.util.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class JavaClassGeneratorVisitor implements Visitor {
  private final List<Artifact> artifacts;
  //    private final Stack<ClassWriter> classWriterStack;
  private MethodVisitor mv;
  private ClassWriter cw;
  private LocalVariablesSorter lvs;
  private Map<String, Integer> locals;

  private int quoteDepth;

  private String className;

  private final Environment environment;

  public JavaClassGeneratorVisitor(Environment environment, ClassWriter cw) {
    this.cw = cw;
    this.artifacts = new ArrayList<>();
    this.environment = environment;
    this.quoteDepth = 0;
  }

  public JavaClassGeneratorVisitor(Environment environment) {
    this.cw = new ClassWriter(COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    this.artifacts = new ArrayList<>();
    this.environment = environment;
    this.quoteDepth = 0;
  }

  @Override
  public void visit(BooleanNode node) {
    mv.visitLdcInsn(node.getValue());
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
  }

  @Override
  public void visit(FloatNode node) {
    mv.visitLdcInsn(node.getValue());
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
  }

  @Override
  public void visit(IfNode node) {

    // If inside a quote, don't evaluate
    if (quoteDepth > 0) {
      node.quote().accept(this);
      return;
    }

    node.getCondition().accept(this);

    mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
    Label startOfElseLabel = new Label();
    Label endOfIfLabel = new Label();
    // Compare result of condition
    mv.visitJumpInsn(IFEQ, startOfElseLabel);

    // Compile true block
    node.getLeft().accept(this);

    // Jump past the false block after entering the true block if it exists
    mv.visitJumpInsn(GOTO, endOfIfLabel);
    mv.visitLabel(startOfElseLabel);

    // Compile false block if it exists
    if (node.getRight() != null) {
      node.getRight().accept(this);
    } else {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitLabel(endOfIfLabel);
  }

  @Override
  public void visit(IntegerNode node) {
    mv.visitLdcInsn(node.getValue());
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
  }

  @Override
  public void visit(LambdaNode node) {
    ClassWriter originalCw = this.cw;
    LocalVariablesSorter originalLvs = this.lvs;
    Map<String, Integer> originalLocals = this.locals;
    String originalClassName = this.className;
    this.cw = new ClassWriter(COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    this.className = node.getName();
    cw.visit(
        V1_5,
        ACC_PUBLIC + ACC_MODULE,
        className,
        null,
        "java/lang/Object",
        new String[] {"ie/francis/fspnew/builtin/type/Lambda"});

    int parameterCount = node.getParameters().size();
    String descriptor = "(" + "Ljava/lang/Object;".repeat(parameterCount) + ")Ljava/lang/Object;";
    MethodVisitor originalMv = mv;
    //    mv = cw.visitMethod(ACC_PUBLIC, "<init>", descriptor, null, null);
    //    mv.visitCode();
    //    mv.visitVarInsn(ALOAD, 0);
    //    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    //
    //    for (int i = 0; i < node.getParameters().size(); i++) {
    //      SymbolNode symbol = node.getParameters().get(i);
    //      mv.visitVarInsn(ALOAD, 0);
    //      mv.visitVarInsn(ALOAD, i+1);
    //      mv.visitFieldInsn(PUTFIELD, className, symbol.getValue(), "Ljava/lang/Object;");
    //    }
    //
    //    mv.visitInsn(RETURN);
    //    mv.visitMaxs(0, 0);
    //    mv.visitEnd();

    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    for (int i = 0; i < 3; i++) {
      if (i == parameterCount) {
        continue;
      }
      addUnimplementedCall(i);
    }

    // Unimplemented variadic function, arity >= 4 requires a variadic function
    addUnimplementedCall(4);

    mv = cw.visitMethod(ACC_PUBLIC, "call", descriptor, null, null);
    locals = new HashMap<>();
    locals.put("this", 0);
    for (SymbolNode symbol : node.getParameters()) {
      locals.put(symbol.getValue(), locals.size());
      // cw.visitField(ACC_PRIVATE, symbol.getValue(), "Ljava/lang/Object;", null, null).visitEnd();
    }
    mv.visitCode();

    node.getBody().accept(this);

    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
    this.artifacts.add(new Artifact(node.getName(), cw.toByteArray()));
    cw = originalCw;
    mv = originalMv;
    lvs = originalLvs;
    locals = originalLocals;
    className = originalClassName;

    if (mv != null) {
      mv.visitTypeInsn(Opcodes.NEW, node.getName());
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, node.getName(), "<init>", "()V", false);
    }
  }

  private void addUnimplementedCall(int arity) {
    String descriptor;
    if (arity >= 0 && arity < 4) {
      descriptor = "(" + "Ljava/lang/Object;".repeat(arity) + ")Ljava/lang/Object;";
    } else {
      descriptor = "(" + "Ljava/lang/Object;".repeat(3) + "[Ljava/lang/Object;)Ljava/lang/Object;";
    }
    mv = cw.visitMethod(ACC_PUBLIC, "call", descriptor, null, null);
    mv.visitCode();
    mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fspnew/exception/NotImplementedException");
    mv.visitInsn(DUP);
    mv.visitLdcInsn(String.format("method with arity %d is not implemented", arity));
    mv.visitMethodInsn(
        INVOKESPECIAL,
        "ie/francis/fspnew/exception/NotImplementedException",
        "<init>",
        "(Ljava/lang/String;)V",
        false);
    mv.visitInsn(ATHROW);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  @Override
  public void visit(LetNode node) {
    node.getValue().accept(this);
    mv.visitVarInsn(ASTORE, locals.size());
    mv.visitVarInsn(ALOAD, locals.size());
    String symbol = node.getSymbol().getValue();
    locals.put(symbol, locals.size());
  }

  @Override
  public void visit(ListNode list) {

    List<Node> nodes = list.getNodes();

    // If inside a quote, don't evaluate
    if (quoteDepth > 0) {
      generateCons(nodes);
      return;
    }

    // If outside a quote and the first node is lambda
    if (nodes.get(0).type() == LAMBDA_NODE) {
      generateLambdaAndCall(nodes);
      return;
    }

    for (Node node : nodes) {
      node.accept(this);
    }
  }

  private void generateLambdaAndCall(List<Node> nodes) {
    // Compile lambda
    LambdaNode lambda = (LambdaNode) nodes.get(0);
    lambda.accept(this);

    String owner = lambda.getName();
    String descriptor = "(" + "Ljava/lang/Object;".repeat(nodes.size() - 1) + ")V";
    mv.visitTypeInsn(Opcodes.NEW, owner);
    mv.visitInsn(DUP);

    for (int i = 1; i < nodes.size(); i++) {
      nodes.get(i).accept(this);
    }

    mv.visitMethodInsn(INVOKESPECIAL, owner, "<init>", descriptor, false);

    mv.visitMethodInsn(INVOKEVIRTUAL, owner, "call", lambda.descriptor(), false);
  }

  private void generateCons(List<Node> nodes) {

    for (Node node : nodes) {
      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fspnew/builtin/type/Cons");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(
          INVOKESPECIAL, "ie/francis/fspnew/builtin/type/Cons", "<init>", "()V", false);

      node.accept(this);
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/fspnew/builtin/type/Cons",
          "setCar",
          "(Ljava/lang/Object;)Lie/francis/fspnew/builtin/type/Cons;",
          false);
    }

    for (int i = 1; i < nodes.size(); i++) {
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/fspnew/builtin/type/Cons",
          "setCdr",
          "(Lie/francis/fspnew/builtin/type/Cons;)Lie/francis/fspnew/builtin/type/Cons;",
          false);
    }
  }

  @Override
  public void visit(QuoteNode node) {
    quoteDepth++;
    node.getValue().quote().accept(this);
    quoteDepth--;
  }

  @Override
  public void visit(StringNode node) {
    mv.visitLdcInsn(node.getValue());
  }

  @Override
  public void visit(SymbolNode node) {
    String name = node.getValue();
    if (quoteDepth > 0) {
      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fspnew/builtin/type/Symbol");
      mv.visitInsn(DUP);
      mv.visitLdcInsn(name);
      mv.visitMethodInsn(
          INVOKESPECIAL,
          "ie/francis/fspnew/builtin/type/Symbol",
          "<init>",
          "(Ljava/lang/String;)V",
          false);
      return;
    }
    if (locals.containsKey(name)) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(Opcodes.GETFIELD, className, node.getValue(), "Ljava/lang/Object;");
    } else if (environment.contains(name)) {
      Object value = environment.get(name);
      if (value instanceof Node) {
        ((Node) value).accept(this);
      }
    }
  }

  public List<Artifact> getArtifacts() {
    return artifacts;
  }
}
