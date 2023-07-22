/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.sym;

import static ie.francis.fsp.sym.SymbolType.NUMBER;

public class NumberSymbol implements Symbol {

  private final String name;

  public NumberSymbol(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String descriptor() {
    return "I";
  }

  @Override
  public SymbolType type() {
    return NUMBER;
  }
}
