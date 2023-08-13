/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.rmacro;

import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.runtime.type.Symbol;

public class DerefReaderMacro implements ReaderMacro {

  public DerefReaderMacro() {}

  @Override
  public Object expand(Object object) {
    ConsBuilder consBuilder = new ConsBuilder();
    consBuilder.add(new Symbol("quote"));
    ConsBuilder consBuilder2 = new ConsBuilder();
    consBuilder2.add("deref");
    consBuilder2.add(object);
    consBuilder.add(consBuilder2.getCons());
    return consBuilder.getCons();
  }

  @Override
  public String character() {
    return "@";
  }
}
