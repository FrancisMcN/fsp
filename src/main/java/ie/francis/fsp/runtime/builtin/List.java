/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;
import ie.francis.fsp.runtime.type.Symbol;
import java.util.ArrayList;

public class List extends Function {

  public List() {
    super(
        String.format("%s.run", List.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(java.util.List.of(new Symbol("&rest"), new Symbol("values"))));
  }

  public static Object run(Object object) {
    return object;
  }
}
