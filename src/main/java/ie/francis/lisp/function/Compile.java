/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.compiler.Compiler;
import ie.francis.lisp.exception.NotImplementedException;

public class Compile implements Lambda {
  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {
    Compiler compiler = new Compiler();
    return compiler.compile(arg);
  }
}
