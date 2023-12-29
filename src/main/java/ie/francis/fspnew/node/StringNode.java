/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.STRING_NODE;

import ie.francis.fspnew.visitor.Visitor;

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
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Node quote() {
    return this;
  }

  @Override
  public String toString() {
    return String.format("(string node: %s)", value);
  }

  public String getValue() {
    return value;
  }
}
