/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;

public class Equals extends Function {

  public Equals(String name, String descriptor) {
    super(name, descriptor);
  }

  public Equals() {
    super(
        String.format("%s.run", Equals.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object a, Object b) {
    if (a instanceof Integer) {
      return ((Integer) a).compareTo((Integer) b) == 0;
    }
    return a.equals(b);
  }
}
