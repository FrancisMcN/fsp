/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.visitor;

import static ie.francis.fspnew.node.NodeType.LAMBDA_NODE;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fspnew.compiler.Artifact;
import ie.francis.fspnew.node.*;
import java.util.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class JavaClassGeneratorVisitor implements Visitor {
  private final List<Artifact> artifacts;
  //    private final Stack<ClassWriter> classWriterStack;
  private MethodVisitor mv;
  private ClassWriter cw;
  private LocalVariablesSorter lvs;
  private Map<String, Integer> locals;

  //    public JavaClassGeneratorVisitor(ClassWriter cw) {
  ////        this.classWriterStack = new Stack<>();
  ////        this.classWriterStack.push(cw);
  //        this.artifacts = new ArrayList<>();
  //    }

  public JavaClassGeneratorVisitor() {
    //        this.classWriterStack = new Stack<>();
    this.cw = new ClassWriter(COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    this.artifacts = new ArrayList<>();
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
  public void visit(IfNode node) {}

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
    this.cw = new ClassWriter(COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        ACC_PUBLIC + ACC_MODULE,
        String.format("%s", node.getName()),
        null,
        "java/lang/Object",
        new String[] {});

    int parameterCount = node.getParameters().size();
    String descriptor = "(" + "Ljava/lang/Object;".repeat(parameterCount) + ")Ljava/lang/Object;";
    MethodVisitor originalMv = mv;
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", descriptor, null, null);
    lvs = new LocalVariablesSorter(0, "()Ljava/lang/Object;", mv);
    locals = new HashMap<>();
    for (SymbolNode symbol : node.getParameters()) {
      locals.put(symbol.getValue(), locals.size());
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
  }

  @Override
  public void visit(LetNode node) {}

  @Override
  public void visit(ListNode list) {

    List<Node> nodes = list.getNodes();

    // Check if first node is lambda
    if (nodes.get(0).type() == LAMBDA_NODE) {
      // Compile lambda
      LambdaNode lambda = (LambdaNode) nodes.get(0);
      lambda.accept(this);
      // Call lambda with remaining nodes as args
      for (int i = 1; i < nodes.size(); i++) {
        nodes.get(i).accept(this);
      }
      String owner = lambda.getName();
      mv.visitMethodInsn(INVOKESTATIC, owner, "run", lambda.descriptor(), false);
      return;
    }

    for (Node node : nodes) {
      node.accept(this);
    }
  }

  @Override
  public void visit(QuoteNode node) {}

  @Override
  public void visit(StringNode node) {
    mv.visitLdcInsn(node.getValue());
  }

  @Override
  public void visit(SymbolNode node) {
    String name = node.getValue();
    if (locals.containsKey(name)) {
      mv.visitVarInsn(ALOAD, locals.get(name));
    }
  }

  public List<Artifact> getArtifacts() {
    return artifacts;
  }
}
