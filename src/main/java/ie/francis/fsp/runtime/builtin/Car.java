/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;
import java.util.ArrayList;
import java.util.List;

public class Car extends Function {

  public Car() {
    super(
        String.format("%s.run", Car.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("cons")));
  }

  public static Object run(Object value) {
    return ((Cons) value).getCar();
  }
}
