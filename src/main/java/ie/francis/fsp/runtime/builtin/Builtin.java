/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Builtin {

  public Builtin() {}

  public static int plus(int... nums) {
    int sum = 0;
    for (int val : nums) {
      sum += val;
    }
    System.out.println(sum);
    return sum;
  }

  public static int minus(int... nums) {
    int sum = nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum -= nums[i];
    }
    System.out.println(sum);
    return sum;
  }

  public static int multiply(int... nums) {
    int sum = nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum *= nums[i];
    }
    System.out.println(sum);
    return sum;
  }

  public static int divide(int... nums) {
    int sum = nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum /= nums[i];
    }
    System.out.println(sum);
    return sum;
  }
}
