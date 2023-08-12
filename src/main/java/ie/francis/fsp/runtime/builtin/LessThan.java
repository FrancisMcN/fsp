/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class LessThan {

  public static Object run(Object a, Object b) {
    return ((Integer) a).compareTo((Integer) b) < 0;
  }
}
