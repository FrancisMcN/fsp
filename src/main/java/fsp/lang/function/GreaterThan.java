/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.type.Lambda;

public class GreaterThan extends BaseLambda implements Lambda {
  @Override
  public Object call(Object arg, Object arg2) {
    return (Integer) arg > (Integer) arg2;
  }
}
