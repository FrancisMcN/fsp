/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.parser;

import static ie.francis.fspnew.token.Type.RPAREN;
import static ie.francis.fspnew.token.Type.SYMBOL;

import ie.francis.fspnew.exception.SyntaxErrorException;
import ie.francis.fspnew.node.*;
import ie.francis.fspnew.scanner.Scanner;
import ie.francis.fspnew.token.Token;
import ie.francis.fspnew.token.Type;
import java.util.ArrayList;
import java.util.List;

public class Parser {

  private final Scanner scanner;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
  }

  // prog        : expr*
  // expr        : SYMBOL | STRING | NUMBER | BOOLEAN | list
  // list        : '(' seq ')'
  // seq         : expr*
  public List<Node> parse() throws SyntaxErrorException {
    List<Node> exprs = new ArrayList<>();
    while (scanner.hasNext()) {
      exprs.add(expr());
    }
    return exprs;
  }

  // expr : SYMBOL | STRING | NUMBER | BOOLEAN | list
  protected Node expr() throws SyntaxErrorException {
    Token token = scanner.peek();
    switch (token.getType()) {
      case SYMBOL:
        {
          scanner.next();
          return new SymbolNode(token.getValue());
        }
      case STRING:
        {
          scanner.next();
          return new StringNode(token.getValue());
        }
      case NUMBER:
        {
          scanner.next();
          String tokenValue = token.getValue();
          if (tokenValue.contains(".")) {
            return new FloatNode(Float.parseFloat(tokenValue));
          }
          return new IntegerNode(Integer.parseInt(tokenValue));
        }
      case BOOLEAN:
        {
          scanner.next();
          return new BooleanNode(Boolean.parseBoolean(token.getValue()));
        }
      case LPAREN:
        return list();
      default:
        throw new SyntaxErrorException("syntax error");
    }
  }

  // list : '(' seq ')'
  protected Node list() throws SyntaxErrorException {
    scanner.next();

    if (isSpecial()) {
      return specialForm();
    }

    ListNode listNode = new ListNode();
    while (scanner.hasNext()) {
      Token token = scanner.peek();
      if (token.getType() == RPAREN) {
        break;
      }
      listNode.addNode(expr());
    }
    //    Node seq = seq();

    Token token = scanner.peek();
    if (token.getType() != Type.RPAREN) {
      throw new SyntaxErrorException("expected ')', found: " + token);
    }
    scanner.next();

    return listNode;
  }

  private Node specialForm() {
    Token token = scanner.peek();
    switch (token.getValue()) {
      case "if":
        return ifSpecialForm();
      case "quote":
        return quoteSpecialForm();
      case "lambda":
        return lambdaSpecialForm();
    }
    throw new SyntaxErrorException("expected special form, something went wrong");
  }

  private Node lambdaSpecialForm() {
    scanner.next();
    LambdaNode lambdaNode = new LambdaNode();
    List<SymbolNode> parameters = new ArrayList<>();
    scanner.next();
    while (scanner.hasNext() && scanner.peek().getType() == SYMBOL) {
      Token token = scanner.peek();
      parameters.add(new SymbolNode(token.getValue()));
      scanner.next();
    }
    scanner.next();
    lambdaNode.setParameters(parameters);
    lambdaNode.setBody(expr());
    scanner.next();
    return lambdaNode;
  }

  private Node ifSpecialForm() {
    scanner.next();
    IfNode ifNode = new IfNode();
    ifNode.setCondition(expr());
    ifNode.setLeft(expr());
    ifNode.setRight(expr());
    scanner.next();
    return ifNode;
  }

  private Node quoteSpecialForm() {
    scanner.next();
    QuoteNode quote = new QuoteNode(expr());
    if (scanner.peek().getType() != RPAREN) {
      throw new SyntaxErrorException(
          String.format(
              "found incomplete quote special form, expected ')', found: %s", scanner.peek()));
    }
    scanner.next();
    return quote;
  }

  private boolean isSpecial() {
    Token token = scanner.peek();
    if (token.getType() != SYMBOL) {
      return false;
    }
    switch (token.getValue()) {
      case "if":
      case "quote":
      case "lambda":
      case "progn":
        return true;
    }
    return false;
  }

  // seq : expr*
  //  protected Node seq() throws SyntaxErrorException {
  //    while (scanner.hasNext()) {
  //
  //    }
  //    return expr();
  //
  //    ConsBuilder consBuilder = new ConsBuilder();
  //    while (token.getType() == SYMBOL
  //        || token.getType() == Type.STRING
  //        || token.getType() == Type.NUMBER
  //        || token.getType() == Type.BOOLEAN
  //        || token.getType() == Type.LPAREN) {
  //      consBuilder.add(expr());
  //      token = scanner.peek();
  //    }
  //    return consBuilder.getCons();
  //  }

}
