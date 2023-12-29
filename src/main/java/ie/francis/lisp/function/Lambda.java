/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

public interface Lambda {
  Object call();

  Object call(Object arg);
}
