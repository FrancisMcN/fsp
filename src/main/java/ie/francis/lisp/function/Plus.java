/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class Plus implements Lambda {
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
    int sum = 0;
    for (Object arg : args) {
      sum += (Integer) arg;
    }
    return sum;
  }
}
