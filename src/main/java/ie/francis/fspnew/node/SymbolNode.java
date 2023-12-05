/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.SYMBOL_NODE;

import ie.francis.fspnew.visitor.Visitor;

public class SymbolNode implements Node {
  private final String value;

  public SymbolNode(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public NodeType type() {
    return SYMBOL_NODE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("(symbol node: %s)", value);
  }
}
