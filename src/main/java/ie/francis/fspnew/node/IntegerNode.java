/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.INTEGER_NODE;

public class IntegerNode implements Node {
  private final int value;

  public IntegerNode(int value) {
    this.value = value;
  }

  @Override
  public NodeType type() {
    return INTEGER_NODE;
  }

  @Override
  public String toString() {
    return String.format("(integer node: %d)", value);
  }
}
