/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.helper.ConsBuilder;

public class List {

  public static Object run(Object... values) {
    ConsBuilder consBuilder = new ConsBuilder();
    for (Object val : values) {
      consBuilder.add(val);
    }
    return consBuilder.getCons();
  }
}
