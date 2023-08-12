/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Equals {

  public static Object run(Object a, Object b) {
    if (a instanceof Integer) {
      return ((Integer) a).compareTo((Integer) b) == 0;
    }
    return a.equals(b);
  }
}
