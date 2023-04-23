/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

public class VisitorImpl implements Visitor {
  @Override
  public void visit(ExprNode exprNode) {
    System.out.println(exprNode);
  }

  @Override
  public void visit(ListNode listNode) {
    System.out.println(listNode);
    for (Node node : listNode.getNodes()) {
      node.accept(this);
    }
  }

  @Override
  public void visit(NumberNode numberNode) {
    //        System.out.println(numberNode);
  }

  @Override
  public void visit(StringNode stringNode) {
    //        System.out.println(stringNode);
  }

  @Override
  public void visit(SymbolNode symbolNode) {
    //        System.out.println(symbolNode);
  }
}
