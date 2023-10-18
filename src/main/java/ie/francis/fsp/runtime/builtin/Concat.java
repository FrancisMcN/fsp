/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;
import ie.francis.fsp.runtime.type.Symbol;
import java.util.ArrayList;
import java.util.List;

public class Concat extends Function {

  public Concat() {
    super(
        String.format("%s.run", Concat.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of(new Symbol("&rest"), new Symbol("values"))));
  }

  public static Object run(Object object) {
    StringBuilder sb = new StringBuilder();
    Cons cons = ((Cons) object);
    while (cons != null) {
      sb.append(cons.getCar());
      cons = cons.getCdr();
    }
    return sb.toString();
  }
}
