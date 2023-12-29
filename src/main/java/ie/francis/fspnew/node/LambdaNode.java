/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.LAMBDA_NODE;

import ie.francis.fspnew.visitor.Visitor;
import java.util.ArrayList;
import java.util.List;

public class LambdaNode implements Node {

  private List<SymbolNode> parameters;
  private Node body;
  private String name;

  public LambdaNode(List<SymbolNode> parameters, Node body) {
    this.parameters = parameters;
    this.body = body;
    this.name = String.format("Lambda%s", Long.toHexString(Double.doubleToLongBits(Math.random())));
  }

  public LambdaNode() {
    this.parameters = new ArrayList<>();
    this.name = String.format("Lambda%s", Long.toHexString(Double.doubleToLongBits(Math.random())));
  }

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
  public Node quote() {
    ListNode list = new ListNode();
    list.addNode(new SymbolNode("lambda"));
    ListNode parameters = new ListNode();
    for (Node parameter : this.parameters) {
      parameters.addNode(parameter.quote());
    }
    list.addNode(parameters);
    list.addNode(body.quote());
    return list;
  }

  @Override
  public String toString() {
    return String.format("(lambda node: (parameters: %s) (body: %s))", this.parameters, this.body);
  }

  public List<SymbolNode> getParameters() {
    return parameters;
  }

  public Node getBody() {
    return body;
  }

  public String getName() {
    return name;
  }

  public String descriptor() {
    return "(" + "Ljava/lang/Object;".repeat(0) + ")Ljava/lang/Object;";
  }

  public int arity() {
    return this.parameters.size();
  }
}
