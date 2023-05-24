/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.codegen;

import static org.objectweb.asm.Opcodes.ACC_MODULE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassGenerator {

  private ClassWriter cw = new ClassWriter(0);
  private String className;

  public ClassGenerator(String className) {
    this.className = className;
  }

  public byte[] generate() {

    cw.visit(
        V1_5,
        Opcodes.ACC_PUBLIC + ACC_MODULE,
        String.format("ie/francis/%s", className),
        null,
        "java/lang/Object",
        new String[] {});

    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "()V", null, null);
    mv.visitCode();
    mv.visitFieldInsn(
        GETSTATIC,
        "java/lang/System",
        "out",
        "Ljava/io/PrintStream;"); // put System.out to operand stack
    mv.visitLdcInsn(
        "Hello World"); // load const "Hello" from const_pool, and put onto the operand stack
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
    mv.visitInsn(RETURN);
    mv.visitMaxs(2, 1);
    mv.visitEnd();

    cw.visitEnd();

    return cw.toByteArray();
  }
}
