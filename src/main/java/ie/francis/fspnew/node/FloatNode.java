/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.FLOAT_NODE;

import ie.francis.fspnew.visitor.Visitor;

public class FloatNode implements Node {
  private final float value;

  public FloatNode(float value) {
    this.value = value;
  }

  @Override
  public NodeType type() {
    return FLOAT_NODE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("(float node: %f)", value);
  }
}
