/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

import static ie.francis.fsp.ast.NodeType.PROGRAM_NODE;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode implements Node {
  private final List<Node> nodes;

  public ProgramNode() {
    this.nodes = new ArrayList<>();
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
    return PROGRAM_NODE;
  }

  @Override
  public String value() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Node node : this.nodes) {
      sb.append(node.toString());
    }
    return sb.toString();
  }
}
