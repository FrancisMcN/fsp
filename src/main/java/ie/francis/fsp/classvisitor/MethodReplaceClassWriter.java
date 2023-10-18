/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.classvisitor;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ASM9;

import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class MethodReplaceClassWriter extends ClassVisitor {

  MethodCounterClassReader methodCounter;
  ClassWriter cw;

  String className;

  public MethodReplaceClassWriter(int flags) {
    super(flags);
    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    methodCounter = new MethodCounterClassReader(ASM9);
  }

  public MethodReplaceClassWriter(byte[] bytes, int flags) {
    super(flags);
    final ClassReader classReader = new ClassReader(bytes);
    cw = new ClassWriter(classReader, COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

    methodCounter = new MethodCounterClassReader(ASM9);

    classReader.accept(methodCounter, ClassReader.EXPAND_FRAMES);
    classReader.accept(this, ClassReader.EXPAND_FRAMES);
  }

  @Override
  public final MethodVisitor visitMethod(
      final int access,
      final String name,
      final String descriptor,
      final String signature,
      final String[] exceptions) {
    Map<String, Integer> methods = methodCounter.getMethods();
    String methodName = className + name + descriptor;
    if (methods.containsKey(methodName)) {
      Integer count = methodCounter.getMethods().get(methodName);
      if (count > 1) {
        methodCounter.getMethods().put(methodName, count - 1);
        return null;
      }
      methodCounter.getMethods().put(methodName, count - 1);
    }
    return cw.visitMethod(access, name, descriptor, signature, exceptions);
  }

  @Override
  public void visit(
      int version,
      int access,
      String name,
      String signature,
      String superName,
      String[] interfaces) {
    className = name;
    cw.visit(version, access, name, signature, superName, interfaces);
  }

  public byte[] toByteArray() {
    return cw.toByteArray();
  }
}
