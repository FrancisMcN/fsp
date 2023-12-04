/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.QUOTE_NODE;

public class QuoteNode implements Node {

  private final Node value;

  public QuoteNode(Node value) {
    this.value = value;
  }

  @Override
  public NodeType type() {
    return QUOTE_NODE;
  }

  @Override
  public String toString() {
    return String.format("(quote node: %s)", value);
  }
}
