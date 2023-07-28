/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import ie.francis.fsp.ast.*;
import java.util.List;

public class StringVisitor implements Visitor {

  private final StringBuilder sb;

  public StringVisitor() {
    this.sb = new StringBuilder();
  }

  @Override
  public void visit(SxprNode sxprNode) {
    sxprNode.getCar().accept(this);
    sxprNode.getCdr().accept(this);
  }

  @Override
  public void visit(ListNode listNode) {
    sb.append("(");
    List<Node> nodes = listNode.getNodes();
    for (int i = 0; i < nodes.size(); i++) {
      nodes.get(i).accept(this);
      if (i < nodes.size() - 1) {
        sb.append(" ");
      }
    }
    sb.append(")");
  }

  @Override
  public void visit(NumberNode numberNode) {
    sb.append(numberNode.value());
  }

  @Override
  public void visit(StringNode stringNode) {
    sb.append(stringNode.value());
  }

  @Override
  public void visit(SymbolNode symbolNode) {
    sb.append(symbolNode.value());
  }

  @Override
  public void visit(BooleanNode booleanNode) {
    sb.append(booleanNode.value());
  }

  public String toString() {
    return sb.toString();
  }
}
