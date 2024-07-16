/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;
import java.util.ArrayList;
import java.util.List;

public class Cdr extends Function {

  public Cdr() {
    super(
        String.format("%s.run", Cdr.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("cons")));
  }

  public static Object run(Object value) {
    return ((Cons) value).getCdr();
  }
}
