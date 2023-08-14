/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;
import java.util.ArrayList;
import java.util.List;

public class GreaterThan extends Function {

  public GreaterThan() {
    super(
        String.format("%s.run", GreaterThan.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("first", "second")));
  }

  public static Object run(Object a, Object b) {
    return ((Integer) a).compareTo((Integer) b) > 0;
  }
}
