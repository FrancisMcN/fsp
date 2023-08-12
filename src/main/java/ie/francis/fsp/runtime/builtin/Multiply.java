/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Multiply {

  public static Object run(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum *= ((Integer) nums[i]);
    }
    return sum;
  }
}
