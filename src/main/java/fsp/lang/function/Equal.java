/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.type.Lambda;

public class Equal extends BaseLambda implements Lambda {
  @Override
  public Object call(Object arg, Object arg2) {
    if (arg == null) {
      return null == arg2;
    }
    return arg.equals(arg2);
  }
}
