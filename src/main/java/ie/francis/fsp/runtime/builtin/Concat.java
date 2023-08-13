/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;

public class Concat extends Function {

  public Concat(String name, String descriptor) {
    super(name, descriptor);
  }

  public Concat() {
    super(
        String.format("%s.run", Concat.class.getCanonicalName().replace(".", "/")),
        "([Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object... values) {
    StringBuilder sb = new StringBuilder();
    for (Object val : values) {
      sb.append(val);
    }
    return sb.toString();
  }
}
