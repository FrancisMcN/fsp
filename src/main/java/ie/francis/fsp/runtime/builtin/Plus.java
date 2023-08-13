/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;

public class Plus extends Function {

  public Plus(String name, String descriptor) {
    super(name, descriptor);
  }

  public Plus() {
    super(
        String.format("%s.run", Plus.class.getCanonicalName().replace(".", "/")),
        "([Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object... nums) {
    Integer sum = 0;
    for (Object o : nums) {
      sum += ((Integer) o);
    }
    return sum;
  }
}
