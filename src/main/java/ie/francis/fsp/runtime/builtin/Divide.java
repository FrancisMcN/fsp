/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;

public class Divide extends Function {

  public Divide(String name, String descriptor) {
    super(name, descriptor);
  }

  public Divide() {
    super(
        String.format("%s.run", Divide.class.getCanonicalName().replace(".", "/")),
        "([Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum /= ((Integer) nums[i]);
    }
    return sum;
  }
}
