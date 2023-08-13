/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.rmacro;

import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.runtime.type.Symbol;

public class QuoteReaderMacro implements ReaderMacro {

  public QuoteReaderMacro() {}

  @Override
  public Object expand(Object object) {
    ConsBuilder consBuilder = new ConsBuilder();
    consBuilder.add(new Symbol("quote"));
    consBuilder.add(object);
    return consBuilder.getCons();
  }

  @Override
  public String character() {
    return "'";
  }
}
