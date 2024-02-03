/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class Minus implements Lambda {
  @Override
  public Object call() {
    return 0;
  }

  @Override
  public Object call(Object arg) {
    return (Number) arg;
  }

  @Override
  public Object call(Object[] args) {
    int sum = (Integer) args[0];
    for (int i = 1; i < args.length; i++) {
      sum -= (Integer) args[i];
    }
    return sum;
  }
}
