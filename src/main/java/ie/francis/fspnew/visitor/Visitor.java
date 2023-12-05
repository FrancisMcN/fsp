/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.visitor;

import ie.francis.fspnew.node.*;

public interface Visitor {
  void visit(BooleanNode node);

  void visit(FloatNode node);

  void visit(IfNode node);

  void visit(IntegerNode node);

  void visit(LambdaNode node);

  void visit(LetNode node);

  void visit(ListNode node);

  void visit(QuoteNode node);

  void visit(StringNode node);

  void visit(SymbolNode node);
}
