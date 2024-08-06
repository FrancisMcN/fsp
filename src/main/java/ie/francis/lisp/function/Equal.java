/*
 * (c) 2024 Francis McNamee
 * */
 
package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class Equal extends BaseLambda implements Lambda {
  @Override
  public Object call(Object arg, Object arg2) {
    if (arg == null) {
      return null == arg2;
    }
    return arg.equals(arg2);
  }
}
