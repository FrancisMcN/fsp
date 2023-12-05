/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.LAMBDA_NODE;

import ie.francis.fspnew.visitor.Visitor;
import java.util.List;

public class LambdaNode implements Node {

  private List<SymbolNode> parameters;
  private Node body;

  public LambdaNode(List<SymbolNode> parameters, Node body) {
    this.parameters = parameters;
    this.body = body;
  }

  public LambdaNode() {}

  public void setParameters(List<SymbolNode> parameters) {
    this.parameters = parameters;
  }

  public void setBody(Node body) {
    this.body = body;
  }

  @Override
  public NodeType type() {
    return LAMBDA_NODE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("(lambda node: (parameters: %s) (body: %s))", this.parameters, this.body);
  }
}
