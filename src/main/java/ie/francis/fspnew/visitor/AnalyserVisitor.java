/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.visitor;

import static ie.francis.fspnew.analyser.SymbolType.*;

import ie.francis.fspnew.analyser.SymbolTable;
import ie.francis.fspnew.analyser.SymbolType;
import ie.francis.fspnew.node.*;
import java.util.Stack;

public class AnalyserVisitor implements Visitor {

  private final SymbolTable symbolTable;
  private final Stack<SymbolType> stack;

  public AnalyserVisitor(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.stack = new Stack<>();
  }

  @Override
  public void visit(BooleanNode node) {
    stack.push(BOOLEAN);
  }

  @Override
  public void visit(FloatNode node) {
    stack.push(FLOAT);
  }

  @Override
  public void visit(IfNode node) {
    node.getLeft().accept(this);
    if (node.getRight() != null) {
      node.getRight().accept(this);

      SymbolType leftBranch = stack.pop();
      SymbolType rightBranch = stack.pop();
      if (leftBranch == rightBranch) {
        stack.push(leftBranch);
      } else {
        stack.push(ANY);
      }
    }
  }

  @Override
  public void visit(IntegerNode node) {
    stack.push(INTEGER);
  }

  @Override
  public void visit(LambdaNode node) {
    stack.push(LAMBDA);
  }

  @Override
  public void visit(LetNode node) {
    SymbolNode symbol = node.getSymbol();
    node.getValue().accept(this);
    symbolTable.add(symbol.getValue(), stack.pop());
  }

  @Override
  public void visit(ListNode node) {
    stack.push(CONS);
  }

  @Override
  public void visit(QuoteNode node) {}

  @Override
  public void visit(StringNode node) {
    stack.push(STRING);
  }

  @Override
  public void visit(SymbolNode node) {
    SymbolType symbolType = symbolTable.get(node.getValue());
    stack.push(symbolType);
  }
}
