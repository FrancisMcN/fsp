/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.visitor;

import static ie.francis.fspnew.node.NodeType.LAMBDA_NODE;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fspnew.compiler.Artifact;
import ie.francis.fspnew.generator.LambdaGenerator;
import ie.francis.fspnew.node.*;
import ie.francis.fspnew.repl.Environment;
import java.util.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JavaClassGeneratorVisitor implements Visitor {

  private final List<Artifact> artifacts;

  private MethodVisitor mv;
  private final Map<String, Integer> locals;

  private int quoteDepth;

  private final Environment environment;

  public void setMv(MethodVisitor mv) {
    this.mv = mv;
  }

  public void addLocal(String local) {
    if (!this.locals.containsKey(local)) {
      this.locals.put(local, this.locals.size());
    }
  }

  public JavaClassGeneratorVisitor(Environment environment) {
    this.locals = new HashMap<>();
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

    if (quoteDepth > 0) {
      node.quote().accept(this);
      return;
    }

    LambdaGenerator generator = new LambdaGenerator(node, environment);
    generator.generate();
    if (mv != null) {
      mv.visitTypeInsn(Opcodes.NEW, node.getName());
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, node.getName(), "<init>", "()V", false);
    }
    this.artifacts.addAll(generator.getArtifacts());
  }

  @Override
  public void visit(LetNode node) {

    if (quoteDepth > 0) {
      node.quote().accept(this);
      return;
    }

    environment.put(node.getSymbol().getValue(), node.eval());
    //      return;
    //    }

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
    for (int i = 1; i < nodes.size(); i++) {
      nodes.get(i).accept(this);
    }
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
      mv.visitVarInsn(ALOAD, locals.get(name));
    } else if (environment.contains(name)) {
      Object value = environment.get(name);
      if (value instanceof Integer) {
        mv.visitLdcInsn((Integer) value);
      }
      //      if (value instanceof Lambda) {
      //        mv.visitTypeInsn(Opcodes.NEW, name);
      //        mv.visitInsn(DUP);
      //        mv.visitMethodInsn(INVOKESPECIAL, name, "<init>", "()V", false);
      //      }
      //      if (value instanceof Node) {
      //        ((Node) value).accept(this);

      //      }
    }
  }

  public List<Artifact> getArtifacts() {
    return artifacts;
  }
}
