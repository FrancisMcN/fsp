/*
 * (c) 2024 Francis McNamee
 * */
 
package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class Minus extends BaseLambda implements Lambda {
  @Override
  public Object call() {
    return 0;
  }

  @Override
  public Object call(Object arg) {
    return -(Integer) arg;
  }

  @Override
  public Object call(Object arg, Object arg2) {
    return (Integer) arg - (Integer) arg2;
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3) {
    return (Integer) arg - (Integer) arg2 - (Integer) arg3;
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4) {
    return (Integer) arg - (Integer) arg2 - (Integer) arg3 - (Integer) arg4;
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4, Object arg5) {
    return (Integer) arg - (Integer) arg2 - (Integer) arg3 - (Integer) arg4 - (Integer) arg4;
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
