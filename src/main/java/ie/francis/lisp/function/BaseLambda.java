/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.exception.NotImplementedException;
import ie.francis.lisp.type.Lambda;

public class BaseLambda implements Lambda {
  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg, Object arg2) {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3) {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4) {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4, Object arg5) {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object[] args) {
    throw new NotImplementedException("method not implemented");
  }
}
