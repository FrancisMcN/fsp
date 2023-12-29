/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.BOOLEAN_NODE;

import ie.francis.fspnew.visitor.Visitor;

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
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Node quote() {
    return this;
  }

  @Override
  public String toString() {
    return String.format("(boolean node: %b)", value);
  }

  public boolean getValue() {
    return this.value;
  }
}
