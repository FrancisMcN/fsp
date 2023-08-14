/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;
import java.util.ArrayList;
import java.util.List;

public class Println extends Function {
  public Println() {
    super(
        String.format("%s.run", Println.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("value")));
  }

  public static Object run(Object value) {
    System.out.println(value);
    return null;
  }
}
