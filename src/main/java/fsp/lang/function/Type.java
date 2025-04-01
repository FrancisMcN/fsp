/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.type.Lambda;

public class Type extends BaseLambda implements Lambda {

  @Override
  public Object call(Object arg) {
    if (arg != null) {
      return arg.getClass().getName();
    }
    return null;
  }
}
