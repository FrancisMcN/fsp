/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.QUOTE_NODE;

import ie.francis.fspnew.builtin.type.Cons;
import ie.francis.fspnew.builtin.type.Symbol;
import ie.francis.fspnew.visitor.Visitor;

public class QuoteNode implements Node {

  private final Node value;

  public QuoteNode(Node value) {
    this.value = value;
  }

  @Override
  public NodeType type() {
    return QUOTE_NODE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Object eval() {
    Cons cons = new Cons();
    cons.setCar(new Symbol("quote"));
    cons.setCdr(new Cons().setCar(value.eval()));
    return null;
  }

  @Override
  public Node quote() {
    ListNode list = new ListNode();
    list.addNode(new SymbolNode("quote"));
    list.addNode(value.quote());
    return list;
  }

  @Override
  public String toString() {
    return String.format("(quote node: %s)", value);
  }

  public Node getValue() {
    return value;
  }
}
