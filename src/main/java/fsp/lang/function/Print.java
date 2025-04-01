/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.exception.NotImplementedException;
import fsp.lang.type.Lambda;

public class Print extends BaseLambda implements Lambda {
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
