/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.type.Lambda;

public class Eval extends BaseLambda implements Lambda {

  @Override
  public Object call(Object arg) {
    Compile compiler = new Compile();
    Lambda lambda = (Lambda) compiler.call(arg);
    return lambda.call();
  }
}
