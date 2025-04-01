/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.type.Lambda;

public class Plus extends BaseLambda implements Lambda {
  @Override
  public Object call() {
    return 0;
  }

  @Override
  public Object call(Object arg) {
    return (Integer) arg;
  }

  @Override
  public Object call(Object arg, Object arg2) {
    return (Integer) arg + (Integer) arg2;
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3) {
    return (Integer) arg + (Integer) arg2 + (Integer) arg3;
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4) {
    return (Integer) arg + (Integer) arg2 + (Integer) arg3 + (Integer) arg4;
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4, Object arg5) {
    return (Integer) arg + (Integer) arg2 + (Integer) arg3 + (Integer) arg4 + (Integer) arg4;
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
