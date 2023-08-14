/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;
import java.util.ArrayList;
import java.util.List;

public class Multiply extends Function {
  public Multiply() {
    super(
        String.format("%s.run", Multiply.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("&rest", "values")));
  }

  public static Object run(Object object) {
    Cons cons = ((Cons) object);
    Integer sum = (Integer) cons.getCar();
    cons = cons.getCdr();
    while (cons != null) {
      sum *= ((Integer) cons.getCar());
      cons = cons.getCdr();
    }
    return sum;
  }
}
