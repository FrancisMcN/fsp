/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.generator;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fspnew.compiler.Artifact;
import ie.francis.fspnew.node.LambdaNode;
import ie.francis.fspnew.node.SymbolNode;
import ie.francis.fspnew.repl.Environment;
import ie.francis.fspnew.visitor.JavaClassGeneratorVisitor;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LambdaGenerator {

  private final LambdaNode lambda;
  private final JavaClassGeneratorVisitor visitor;
  private final List<Artifact> artifacts;

  public LambdaGenerator(LambdaNode lambda, Environment environment) {
    this.lambda = lambda;
    this.visitor = new JavaClassGeneratorVisitor(environment);
    this.artifacts = new ArrayList<>();
  }

  public void generate() {
    // Setup class
    ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        ACC_PUBLIC + ACC_MODULE,
        lambda.getName(),
        null,
        "java/lang/Object",
        new String[] {"ie/francis/fspnew/builtin/type/Lambda"});

    // Add a constructor to the generated class
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    String descriptor = "(" + "Ljava/lang/Object;".repeat(lambda.arity()) + ")Ljava/lang/Object;";
    mv = cw.visitMethod(ACC_PUBLIC, "call", descriptor, null, null);
    visitor.setMv(mv);
    visitor.addLocal("this");
    for (SymbolNode symbol : lambda.getParameters()) {
      visitor.addLocal(symbol.getValue());
    }
    mv.visitCode();

    lambda.getBody().accept(visitor);

    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    // Add dummy implementations for other arities to satisfy Lambda interface
    for (int i = 0; i < 3; i++) {
      if (i == lambda.arity()) {
        continue;
      }
      addUnimplementedLambdaCall(cw, i);
    }
    // Unimplemented variadic function, arity >= 4 requires a variadic function
    addUnimplementedLambdaCall(cw, 4);

    cw.visitEnd();

    List<Artifact> artifacts = visitor.getArtifacts();
    artifacts.add(new Artifact(lambda.getName(), cw.toByteArray()));
    this.artifacts.addAll(visitor.getArtifacts());
  }

  public List<Artifact> getArtifacts() {
    return artifacts;
  }

  private void addUnimplementedLambdaCall(ClassWriter cw, int arity) {
    String descriptor;
    if (arity >= 0 && arity < 4) {
      descriptor = "(" + "Ljava/lang/Object;".repeat(arity) + ")Ljava/lang/Object;";
    } else {
      descriptor = "(" + "Ljava/lang/Object;".repeat(3) + "[Ljava/lang/Object;)Ljava/lang/Object;";
    }
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "call", descriptor, null, null);
    mv.visitCode();
    mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fspnew/exception/NotImplementedException");
    mv.visitInsn(DUP);
    mv.visitLdcInsn("method is not implemented");
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
}
