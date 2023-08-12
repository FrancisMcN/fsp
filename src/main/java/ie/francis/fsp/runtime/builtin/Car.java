/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Cons;

public class Car {

  public static Object run(Object value) {
    return ((Cons) value).getCar();
  }
}
