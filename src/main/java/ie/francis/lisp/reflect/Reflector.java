/*
 * (c) 2025 Francis McNamee
 * */
 
package ie.francis.lisp.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflector {

  public Reflector() {}

  public static Object invoke(Object target, String methodName, Object[] args)
      throws InvocationTargetException, IllegalAccessException {
    Method method = findMethod(target.getClass(), methodName, args.length);
    return method.invoke(target, args);
  }

  public static Object invoke(Object target, String methodName)
      throws InvocationTargetException, IllegalAccessException {
    Method method = findMethod(target.getClass(), methodName, 0);
    return method.invoke(target);
  }

  private static Method findMethod(Class<?> clazz, String name, int argCount) {
    for (Method method : clazz.getMethods()) {
      if (method.getName().equalsIgnoreCase(name) && method.getParameters().length == argCount) {
        return method;
      }
    }
    throw new RuntimeException(String.format("method '%s' not found", name));
  }
}
