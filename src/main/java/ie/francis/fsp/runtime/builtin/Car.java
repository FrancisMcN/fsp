/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;

public class Car extends Function {

  public Car(String name, String descriptor) {
    super(name, descriptor);
  }

  public Car() {
    super(
        String.format("%s.run", Car.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object value) {
    return ((Cons) value).getCar();
  }
}
