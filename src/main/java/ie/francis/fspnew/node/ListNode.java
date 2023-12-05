/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.LIST_NODE;

import ie.francis.fspnew.visitor.Visitor;
import java.util.ArrayList;
import java.util.List;

public class ListNode implements Node {

  private List<Node> nodes;

  public ListNode() {
    this.nodes = new ArrayList<>();
  }

  public void addNode(Node node) {
    this.nodes.add(node);
  }

  @Override
  public NodeType type() {
    return LIST_NODE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("(list node: %s)", nodes);
  }
}
