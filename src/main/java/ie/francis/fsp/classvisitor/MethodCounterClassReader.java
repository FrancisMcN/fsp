/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.classvisitor;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodCounterClassReader extends ClassVisitor {
  String className;
  Map<String, Integer> methods;

  protected MethodCounterClassReader(int api) {
    super(api);
    methods = new HashMap<>();
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {
    String methodName = className + name + descriptor;
    if (!methods.containsKey(methodName)) {
      methods.put(methodName, 1);
    } else {
      Integer val = methods.get(methodName);
      methods.put(methodName, val + 1);
    }
    return super.visitMethod(access, name, descriptor, signature, exceptions);
  }

  @Override
  public void visit(
      int version,
      int access,
      String name,
      String signature,
      String superName,
      String[] interfaces) {
    this.className = name;
    super.visit(version, access, name, signature, superName, interfaces);
  }

  public Map<String, Integer> getMethods() {
    return methods;
  }
}
