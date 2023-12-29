/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.compiler;

import static org.objectweb.asm.Opcodes.*;

import ie.francis.lisp.type.Cons;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class QuoteCompiler {

  private final MethodVisitor mv;

  public QuoteCompiler(MethodVisitor mv) {
    this.mv = mv;
  }

  public void compile(Cons cons) {

    Cons head = cons;
    while (cons != null) {

      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/type/Cons");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, "ie/francis/lisp/type/Cons", "<init>", "()V", false);
    }
  }

  //    private void generateCons(List<Node> nodes) {
  //
  //        for (Node node : nodes) {
  //            mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fspnew/builtin/type/Cons");
  //            mv.visitInsn(DUP);
  //            mv.visitMethodInsn(
  //                    INVOKESPECIAL, "ie/francis/fspnew/builtin/type/Cons", "<init>", "()V",
  // false);
  //
  //            node.accept(this);
  //            mv.visitMethodInsn(
  //                    INVOKEVIRTUAL,
  //                    "ie/francis/fspnew/builtin/type/Cons",
  //                    "setCar",
  //                    "(Ljava/lang/Object;)Lie/francis/fspnew/builtin/type/Cons;",
  //                    false);
  //        }
  //
  //        for (int i = 1; i < nodes.size(); i++) {
  //            mv.visitMethodInsn(
  //                    INVOKEVIRTUAL,
  //                    "ie/francis/fspnew/builtin/type/Cons",
  //                    "setCdr",
  //
  // "(Lie/francis/fspnew/builtin/type/Cons;)Lie/francis/fspnew/builtin/type/Cons;",
  //                    false);
  //        }
  //    }

}
