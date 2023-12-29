/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.builtin.function;

import ie.francis.fspnew.builtin.type.Lambda;
import ie.francis.fspnew.exception.NotImplementedException;

public class Plus implements Lambda {
  @Override
  public Object call() {
    throw new NotImplementedException("method is not implemented");
  }

  @Override
  public Object call(Object arg) {
    return (Integer) arg;
  }

  @Override
  public Object call(Object arg1, Object arg2) {
    return (Integer) arg1 + (Integer) arg2;
  }

  @Override
  public Object call(Object arg1, Object arg2, Object arg3) {
    return (Integer) arg1 + (Integer) arg2 + (Integer) arg3;
  }

  @Override
  public Object call(Object arg1, Object arg2, Object arg3, Object... args) {
    int sum = (Integer) arg1 + (Integer) arg2 + (Integer) arg3;
    for (Object arg : args) {
      sum += (Integer) arg;
    }
    return sum;
  }
}
