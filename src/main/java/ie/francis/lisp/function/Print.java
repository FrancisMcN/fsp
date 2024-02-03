/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.exception.NotImplementedException;
import ie.francis.lisp.type.Lambda;

public class Print implements Lambda {
  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {
    if (arg == null) {
      System.out.println("nil");
    } else {
      System.out.println(arg);
    }
    return null;
  }

  @Override
  public Object call(Object[] args) {
    throw new NotImplementedException("method not implemented");
  }
}
