/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Builtin {

  public Builtin() {}

  public static void print(int value) {
    System.out.println(value);
  }

  public static void print(String value) {
    System.out.println(value);
  }

  public static String concat(String... values) {
    StringBuilder sb = new StringBuilder();
    for (String val : values) {
      sb.append(val);
    }
    return sb.toString();
  }

  public static int plus(int... nums) {
    int sum = 0;
    for (int val : nums) {
      sum += val;
    }
    return sum;
  }

  public static int minus(int... nums) {
    int sum = nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum -= nums[i];
    }
    return sum;
  }

  public static int multiply(int... nums) {
    int sum = nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum *= nums[i];
    }
    return sum;
  }

  public static int divide(int... nums) {
    int sum = nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum /= nums[i];
    }
    return sum;
  }
}
