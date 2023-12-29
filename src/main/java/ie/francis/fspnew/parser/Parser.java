/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.parser;

import static ie.francis.fspnew.token.Type.*;

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

  // expr : SYMBOL | STRING | NUMBER | BOOLEAN | list | TICK expr
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
      case TICK:
        {
          scanner.next();
          return new QuoteNode(expr());
        }
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
      case "let":
        return letSpecialForm();
    }
    throw new SyntaxErrorException("expected special form, something went wrong");
  }

  // let_special_form : '(' let SYMBOL expr ')'
  private Node letSpecialForm() {
    scanner.next();
    if (scanner.peek().getType() != SYMBOL) {
      throw new SyntaxErrorException(
          String.format("expecting symbol after 'let', found %s", scanner.peek()));
    }
    LetNode node = new LetNode(new SymbolNode(scanner.next().getValue()), expr());
    if (scanner.peek().getType() != RPAREN) {
      throw new SyntaxErrorException(
          String.format(
              "found incomplete let special form, expected ')', found: %s", scanner.peek()));
    }
    scanner.next();
    return node;
  }

  // lambda_special_form : '(' lambda '(' SYMBOL* ')' expr ')'
  private Node lambdaSpecialForm() {
    scanner.next();
    LambdaNode lambdaNode = new LambdaNode();
    List<SymbolNode> parameters = new ArrayList<>();
    if (scanner.peek().getType() != LPAREN) {
      throw new SyntaxErrorException(
          String.format("expecting parameters list after 'lambda', found %s", scanner.peek()));
    }
    scanner.next();
    while (scanner.hasNext() && scanner.peek().getType() == SYMBOL) {
      Token token = scanner.peek();
      parameters.add(new SymbolNode(token.getValue()));
      scanner.next();
    }
    scanner.next();
    lambdaNode.setParameters(parameters);
    lambdaNode.setBody(expr());
    if (scanner.peek().getType() != RPAREN) {
      throw new SyntaxErrorException(
          String.format(
              "found incomplete lambda special form, expected ')', found: %s", scanner.peek()));
    }
    scanner.next();
    return lambdaNode;
  }

  // if_special_form : '(' if expr expr expr? ')'
  private Node ifSpecialForm() {
    scanner.next();
    IfNode ifNode = new IfNode();
    ifNode.setCondition(expr());
    ifNode.setLeft(expr());
    Token token = scanner.peek();
    switch (token.getType()) {
      case SYMBOL:
      case STRING:
      case NUMBER:
      case BOOLEAN:
      case LPAREN:
        ifNode.setRight(expr());
      default:
        break;
    }
    if (scanner.peek().getType() != RPAREN) {
      throw new SyntaxErrorException(
          String.format(
              "found incomplete if special form, expected ')', found: %s", scanner.peek()));
    }
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
      case "let":
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
