/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.exception.NotImplementedException;
import ie.francis.lisp.type.Lambda;

public class Type implements Lambda {
  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {
    if (arg != null) {
      return arg.getClass().getName();
    }
    return null;
  }

  @Override
  public Object call(Object[] args) {
    throw new NotImplementedException("method not implemented");
  }
}
