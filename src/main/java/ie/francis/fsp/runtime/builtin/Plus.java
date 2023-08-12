/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Plus {

  public static Object run(Object... nums) {
    Integer sum = 0;
    for (Object o : nums) {
      sum += ((Integer) o);
    }
    return sum;
  }
}
