/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class LessThan extends BaseLambda implements Lambda {
  @Override
  public Object call(Object arg, Object arg2) {
    return (Integer) arg < (Integer) arg2;
  }
}
