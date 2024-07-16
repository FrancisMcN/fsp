/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.LIST_NODE;

import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fspnew.visitor.Visitor;
import java.util.ArrayList;
import java.util.List;

public class ListNode implements Node {

  private final List<Node> nodes;

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
  public Object eval() {
    Cons cons = new Cons();
    Cons head = cons;
    for (Node node : this.nodes) {
      cons.setCar(node.eval());
      cons = new Cons();
      cons.setCdr(cons);
    }
    return head;
  }

  @Override
  public Node quote() {
    return this;
  }

  @Override
  public String toString() {
    return String.format("(list node: %s)", nodes);
  }

  public List<Node> getNodes() {
    return nodes;
  }
}
