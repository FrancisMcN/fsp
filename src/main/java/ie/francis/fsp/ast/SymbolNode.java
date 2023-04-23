/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

public class SymbolNode implements Node {

  private final String symbol;

  public SymbolNode(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("Symbol(%s)", this.symbol);
  }
}
