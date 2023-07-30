/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.parser;

import ie.francis.fsp.ast.*;
import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.scanner.Scanner;
import ie.francis.fsp.token.Token;
import ie.francis.fsp.token.Type;

public class Parser {

  private final Scanner scanner;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
  }

  // prog : sxpr*
  // sxpr : atom | list
  // list : '(' sxpr+ ['.' sxpr]? ')'
  // atom : SYMBOL | NUMBER | STRING | BOOLEAN | ε

  public Node parse() throws SyntaxErrorException {
    ProgramNode programNode = new ProgramNode();
    while (scanner.peek().getType() != Type.EOF) {
      programNode.addNode(sxpr());
    }
    return programNode;
  }

  // sxpr : 'sxpr | atom | '(' sxpr '.' sxpr ')' | list
  public Node sxpr() throws SyntaxErrorException {
    Token token = scanner.peek();

    if (token.getType() == Type.QUOTE) {
      scanner.next();
      ListNode node = new ListNode();
      node.addNode(new SymbolNode("quote"));
      node.addNode(sxpr());
      return node;
    }

    if (token.getType() == Type.SYMBOL
        || token.getType() == Type.NUMBER
        || token.getType() == Type.STRING
        || token.getType() == Type.BOOLEAN) {
      return atom();
    }

    Token peek = scanner.peek();
    if (peek.getType() != Type.LPAREN
        && peek.getType() != Type.SYMBOL
        && peek.getType() != Type.NUMBER
        && peek.getType() != Type.STRING
        && peek.getType() != Type.BOOLEAN) {
      throw new SyntaxErrorException(
          String.format("expected '(', symbol, number or a string but found: %s", token.getType()));
    }
    return list();
  }

  // list : '(' sxpr+ ['.' sxpr]? ')'
  public Node list() {
    ListNode list = new ListNode();
    scanner.next();
    do {
      list.addNode(sxpr());
    } while (scanner.peek().getType() != Type.RPAREN && scanner.peek().getType() != Type.DOT);

    Token token = scanner.peek();
    if (token.getType() == Type.DOT) {
      scanner.next();
      SxprNode sxprNode = new SxprNode();
      sxprNode.setCar(list.getNodes().remove(list.getNodes().size() - 1));
      sxprNode.setCdr(sxpr());
      list.addNode(sxprNode);
    }

    if (scanner.peek().getType() != Type.RPAREN) {
      throw new SyntaxErrorException(String.format("expected ')', found: %s", token.getType()));
    }

    scanner.next();
    return list;
  }

  // atom : SYMBOL | NUMBER | STRING | BOOLEAN | ε
  private Node atom() throws SyntaxErrorException {
    Token token = scanner.peek();
    switch (token.getType()) {
      case SYMBOL:
        {
          scanner.next();
          return new SymbolNode(token.getValue());
        }
      case NUMBER:
        {
          scanner.next();
          String tokenValue = token.getValue();
          if (tokenValue.contains(".")) {
            return new NumberNode(Float.parseFloat(tokenValue));
          }
          return new NumberNode(Integer.parseInt(tokenValue));
        }
      case STRING:
        {
          scanner.next();
          return new StringNode(token.getValue());
        }
      case BOOLEAN:
        {
          scanner.next();
          return new BooleanNode(Boolean.parseBoolean(token.getValue()));
        }
      default:
        {
          throw new SyntaxErrorException(
              String.format("expected symbol, number, string or list. Found: %s", token.getType()));
        }
    }
  }
}
