/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Concat {

  public static Object run(Object... values) {
    StringBuilder sb = new StringBuilder();
    for (Object val : values) {
      sb.append(val);
    }
    return sb.toString();
  }
}
