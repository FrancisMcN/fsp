/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class Type extends BaseLambda implements Lambda {

  @Override
  public Object call(Object arg) {
    if (arg != null) {
      return arg.getClass().getName();
    }
    return null;
  }
}
