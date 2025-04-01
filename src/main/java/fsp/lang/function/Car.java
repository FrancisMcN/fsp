/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.type.Cons;
import fsp.lang.type.Lambda;

public class Car extends BaseLambda implements Lambda {

  @Override
  public Object call(Object arg) {
    if (arg instanceof Cons) {
      return ((Cons) arg).getCar();
    }
    return null;
  }
}
