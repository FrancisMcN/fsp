/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import static ie.francis.fspnew.node.NodeType.LET_NODE;

import ie.francis.fspnew.visitor.Visitor;

public class LetNode implements Node {

  private final SymbolNode symbol;
  private final Node value;

  public LetNode(SymbolNode symbol, Node value) {
    this.symbol = symbol;
    this.value = value;
  }

  public SymbolNode getSymbol() {
    return symbol;
  }

  public Node getValue() {
    return value;
  }

  @Override
  public NodeType type() {
    return LET_NODE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("(let node: %s %s)", symbol, value);
  }
}
