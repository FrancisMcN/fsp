/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.sym;

import static ie.francis.fsp.sym.SymbolType.FUNCTION;

public class FunctionSymbol implements Symbol {

  private final String name;
  private final String descriptor;

  public FunctionSymbol(String name, String descriptor) {
    this.name = name;
    this.descriptor = descriptor;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String descriptor() {
    return descriptor;
  }

  @Override
  public SymbolType type() {
    return FUNCTION;
  }
}
