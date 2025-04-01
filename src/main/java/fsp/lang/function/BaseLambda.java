/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.exception.NotImplementedException;
import fsp.lang.type.Lambda;

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
