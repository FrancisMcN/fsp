/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import ie.francis.fsp.runtime.type.*;

import java.lang.Number;

public interface Visitor {

  void visit(Cons cons);
  void visit(FspString fspString);
  void visit(Bool bool);

  void visit(Symbol symbol);

  void visit(Function function);
  void visit(Macro macro);

  void visit(ie.francis.fsp.runtime.type.Number number);


//  void visit(ProgramNode programNode);
//
//  void visit(SxprNode sxprNode);
//
//  void visit(ListNode listNode);
//
//  void visit(NumberNode numberNode);
//
//  void visit(StringNode stringNode);
//
//  void visit(SymbolNode symbolNode);
//
//  void visit(BooleanNode booleanNode);
}
