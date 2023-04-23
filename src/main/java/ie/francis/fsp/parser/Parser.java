/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.parser;

import ie.francis.fsp.ast.ListNode;
import ie.francis.fsp.ast.Node;
import ie.francis.fsp.ast.NumberNode;
import ie.francis.fsp.ast.StringNode;
import ie.francis.fsp.ast.SymbolNode;
import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.scanner.Scanner;
import ie.francis.fsp.token.Token;
import ie.francis.fsp.token.Type;

public class Parser {

  private final Scanner scanner;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
  }

  // expr : SYMBOL | NUMBER | STRING | list
  // list : '(' expr* ')'
  public Node parse() throws SyntaxErrorException {
    return expr();
  }

  private Node expr() throws SyntaxErrorException {
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
      case LPAREN:
        {
          return list();
        }
      default:
        {
          throw new SyntaxErrorException(
              String.format("expected symbol, number, string or list. Found: %s", token.getType()));
        }
    }
  }

  private Node list() {
    ListNode list = new ListNode();
    scanner.next();
    while (scanner.peek().getType() != Type.RPAREN) {
      list.addNode(expr());
    }
    scanner.next();
    return list;
  }
}
