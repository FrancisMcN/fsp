/*
 * (c) 2024 Francis McNamee
 * */
 
package ie.francis.lisp.function;

import ie.francis.lisp.compiler.Compiler;
import ie.francis.lisp.type.Lambda;

public class Compile extends BaseLambda implements Lambda {

  @Override
  public Object call(Object arg) {
    Compiler compiler = new Compiler();
    return compiler.compile(arg);
  }
}
