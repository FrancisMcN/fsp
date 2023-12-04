/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.BOOLEAN_NODE;

public class BooleanNode implements Node {

  private final boolean value;

  public BooleanNode(boolean value) {
    this.value = value;
  }

  @Override
  public NodeType type() {
    return BOOLEAN_NODE;
  }

  @Override
  public String toString() {
    return String.format("(boolean node: %b)", value);
  }
}
