/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

import static ie.francis.fsp.ast.NodeType.LIST_NODE;

import java.util.LinkedList;
import java.util.List;

public class ListNode implements Node {

  private List<Node> nodes;

  public ListNode() {
    nodes = new LinkedList<>();
  }

  public void addNode(Node node) {
    this.nodes.add(node);
  }

  public List<Node> getNodes() {
    return nodes;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public NodeType type() {
    return LIST_NODE;
  }

  @Override
  public String value() {
    return String.format("( %s )", nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("List(");
    for (int i = 0; i < this.nodes.size(); i++) {
      sb.append(nodes.get(i).toString());
      if (i < this.nodes.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
