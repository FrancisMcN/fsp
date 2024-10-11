/*
 * (c) 2024 Francis McNamee
 * */
 
package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class Eval extends BaseLambda implements Lambda {

  @Override
  public Object call(Object arg) {
    Compile compiler = new Compile();
    Lambda lambda = (Lambda) compiler.call(arg);
    return lambda.call();
  }
}
