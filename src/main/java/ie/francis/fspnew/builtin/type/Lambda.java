/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.builtin.type;

public interface Lambda {
  Object call();

  Object call(Object arg);

  Object call(Object arg1, Object arg2);

  Object call(Object arg1, Object arg2, Object arg3);

  Object call(Object arg1, Object arg2, Object arg3, Object... args);
}
