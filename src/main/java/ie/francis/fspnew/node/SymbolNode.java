/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.SYMBOL_NODE;

public class SymbolNode implements Node {
  private final String value;

  public SymbolNode(String value) {
    this.value = value;
  }

  @Override
  public NodeType type() {
    return SYMBOL_NODE;
  }

  @Override
  public String toString() {
    return String.format("(symbol node: %s)", value);
  }
}
