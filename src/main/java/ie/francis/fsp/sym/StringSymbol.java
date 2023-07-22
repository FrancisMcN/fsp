/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.sym;

import static ie.francis.fsp.sym.SymbolType.STRING;

public class StringSymbol implements Symbol {
  private final String name;

  public StringSymbol(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String descriptor() {
    return "Ljava/lang/String";
  }

  @Override
  public SymbolType type() {
    return STRING;
  }
}
