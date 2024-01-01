/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.exception.NotImplementedException;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Lambda;
import ie.francis.lisp.type.Symbol;

public class Type implements Lambda {
  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {
    if (arg instanceof Cons) {
      return "cons";
    } else if (arg instanceof Lambda) {
      return "lambda";
    } else if (arg instanceof Symbol) {
      return "symbol";
    } else if (arg instanceof Integer) {
      return "integer";
    } else if (arg instanceof Float) {
      return "float";
    } else if (arg instanceof Boolean) {
      return "bool";
    } else if (arg instanceof String) {
      return "string";
    }
    return null;
  }
}
