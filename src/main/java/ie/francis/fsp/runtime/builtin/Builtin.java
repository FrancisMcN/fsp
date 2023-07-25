/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Builtin {

  public Builtin() {}

  public static void println(Object value) {
    System.out.println(value);
  }

  public static Object concat(Object... values) {
    StringBuilder sb = new StringBuilder();
    for (Object val : values) {
      sb.append(val);
    }
    return sb.toString();
  }

  public static Object plus(Object... nums) {
    Integer sum = 0;
    for (Object o : nums) {
      sum += ((Integer) o);
    }
    return sum;
  }

  public static Object minus(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum -= ((Integer) nums[i]);
    }
    return sum;
  }

  public static Object multiply(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum *= ((Integer) nums[i]);
    }
    return sum;
  }

  public static Object divide(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum /= ((Integer) nums[i]);
    }
    return sum;
  }
}
