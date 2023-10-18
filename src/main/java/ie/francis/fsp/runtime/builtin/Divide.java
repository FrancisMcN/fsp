/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;
import ie.francis.fsp.runtime.type.Symbol;
import java.util.ArrayList;
import java.util.List;

public class Divide extends Function {

  public Divide() {
    super(
        String.format("%s.run", Divide.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of(new Symbol("&rest"), new Symbol("values"))));
  }

  public static Object run(Object object) {
    Cons cons = ((Cons) object);
    Integer sum = (Integer) cons.getCar();
    cons = cons.getCdr();
    while (cons != null) {
      sum /= ((Integer) cons.getCar());
      cons = cons.getCdr();
    }
    return sum;
  }
}
