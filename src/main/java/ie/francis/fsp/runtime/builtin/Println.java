/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;

public class Println extends Function {

  public Println(String name, String descriptor) {
    super(name, descriptor);
  }

  public Println() {
    super(
        String.format("%s.run", Println.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object value) {
    System.out.println(value);
    return null;
  }
}
