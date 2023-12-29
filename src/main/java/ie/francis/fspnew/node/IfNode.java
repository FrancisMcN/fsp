/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.IF_NODE;

import ie.francis.fspnew.visitor.Visitor;

public class IfNode implements Node {

  private Node condition;
  private Node left;
  private Node right;

  public IfNode(Node condition, Node left, Node right) {
    this.condition = condition;
    this.left = left;
    this.right = right;
  }

  public IfNode() {}

  public void setCondition(Node condition) {
    this.condition = condition;
  }

  public Node getCondition() {
    return condition;
  }

  public Node getLeft() {
    return left;
  }

  public void setLeft(Node left) {
    this.left = left;
  }

  public Node getRight() {
    return right;
  }

  public void setRight(Node right) {
    this.right = right;
  }

  @Override
  public NodeType type() {
    return IF_NODE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Node quote() {
    ListNode list = new ListNode();
    list.addNode(new SymbolNode("if"));
    list.addNode(condition.quote());
    list.addNode(left.quote());
    if (right != null) {
      list.addNode(right.quote());
    }
    return list;
  }

  @Override
  public String toString() {
    return String.format("(if node: %s, left: %s, right: %s)", condition, left, right);
  }
}
