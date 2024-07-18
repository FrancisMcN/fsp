/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.type;

public interface Lambda {
  Object call();

  Object call(Object arg);

  Object call(Object arg, Object arg2);

  Object call(Object arg, Object arg2, Object arg3);

  Object call(Object arg, Object arg2, Object arg3, Object arg4);

  Object call(Object arg, Object arg2, Object arg3, Object arg4, Object arg5);

  Object call(Object[] args);
}
