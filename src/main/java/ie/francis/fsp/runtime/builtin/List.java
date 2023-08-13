/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.runtime.type.Function;

public class List extends Function {

  public List(String name, String descriptor) {
    super(name, descriptor);
  }

  public List() {
    super(
        String.format("%s.run", List.class.getCanonicalName().replace(".", "/")),
        "([Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object... values) {
    ConsBuilder consBuilder = new ConsBuilder();
    for (Object val : values) {
      consBuilder.add(val);
    }
    return consBuilder.getCons();
  }
}
