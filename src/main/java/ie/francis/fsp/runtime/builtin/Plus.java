/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;
import java.util.ArrayList;
import java.util.List;

public class Plus extends Function {
  public Plus() {
    super(
        String.format("%s.run", Plus.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("&rest", "values")));
  }

  public static Object run(Object object) {
    Cons cons = ((Cons) object);
    Integer sum = 0;
    while (cons != null) {
      sum += ((Integer) cons.getCar());
      cons = cons.getCdr();
    }
    return sum;
  }
}
