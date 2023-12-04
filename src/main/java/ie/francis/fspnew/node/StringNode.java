/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.STRING_NODE;

public class StringNode implements Node {
  private final String value;

  public StringNode(String value) {
    this.value = value;
  }

  @Override
  public NodeType type() {
    return STRING_NODE;
  }

  @Override
  public String toString() {
    return String.format("(string node: %s)", value);
  }
}
