/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Lambda;

public class Car extends BaseLambda implements Lambda {

  @Override
  public Object call(Object arg) {
    if (arg instanceof Cons) {
      return ((Cons) arg).getCar();
    }
    return null;
  }
}
