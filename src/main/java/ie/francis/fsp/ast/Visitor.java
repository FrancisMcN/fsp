/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

public interface Visitor {
  void visit(ExprNode exprNode);

  void visit(ListNode listNode);

  void visit(NumberNode numberNode);

  void visit(StringNode stringNode);

  void visit(SymbolNode symbolNode);
}
