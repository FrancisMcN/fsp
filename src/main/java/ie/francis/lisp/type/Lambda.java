/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.type;

public interface Lambda {
  Object call();

  Object call(Object arg);

  Object call(Object[] args);
}
