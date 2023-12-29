/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.visitor;

import static ie.francis.fspnew.analyser.SymbolType.*;
import static ie.francis.fspnew.node.NodeType.LAMBDA_NODE;

import ie.francis.fspnew.analyser.SymbolTable;
import ie.francis.fspnew.exception.TypeErrorException;
import ie.francis.fspnew.node.*;
import java.util.List;
import java.util.Stack;

public class AnalyserVisitor implements Visitor {

  private final SymbolTable symbolTable;
  private final Stack<Node> stack;

  public AnalyserVisitor(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.stack = new Stack<>();
  }

  @Override
  public void visit(BooleanNode node) {
    stack.push(node);
  }

  @Override
  public void visit(FloatNode node) {
    stack.push(node);
  }

  @Override
  public void visit(IfNode node) {
    //    node.getLeft().accept(this);
    //    if (node.getRight() != null) {
    //      node.getRight().accept(this);
    //
    //      SymbolType leftBranch = stack.pop();
    //      SymbolType rightBranch = stack.pop();
    //      if (leftBranch == rightBranch) {
    //        stack.push(leftBranch);
    //      } else {
    //        stack.push(ANY);
    //      }
    //    }
  }

  @Override
  public void visit(IntegerNode node) {
    stack.push(node);
  }

  @Override
  public void visit(LambdaNode node) {
    node.getBody().accept(this);
    stack.push(node);
  }

  @Override
  public void visit(LetNode node) {
    //    SymbolNode symbol = node.getSymbol();
    //    node.getValue().accept(this);
    //    symbolTable.add(symbol.getValue(), stack.pop());
  }

  @Override
  public void visit(ListNode node) {
    List<Node> list = node.getNodes();
    // Found a lambda call
    if (list.get(0).type() == LAMBDA_NODE) {
      int parameterCount = node.getNodes().size() - 1;
      checkLambdaArity((LambdaNode) list.get(0), parameterCount);
    }
    for (int i = 1; i < list.size(); i++) {
      list.get(i).accept(this);
    }
    stack.push(node);
  }

  private void checkLambdaArity(LambdaNode lambda, int suppliedParameters) {
    // Check arity of lambda call
    int arity = lambda.arity();
    if (arity != suppliedParameters) {
      throw new TypeErrorException(
          String.format("expected %d parameters, got %d.", arity, suppliedParameters));
    }
  }

  @Override
  public void visit(QuoteNode node) {}

  @Override
  public void visit(StringNode node) {
    stack.push(node);
  }

  @Override
  public void visit(SymbolNode node) {
    //    SymbolType symbolType = symbolTable.get(node.getValue());
    //    stack.push(symbolType);
  }
}
