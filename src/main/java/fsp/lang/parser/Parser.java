/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.parser;

import fsp.lang.exception.SyntaxErrorException;
import fsp.lang.scanner.Scanner;
import fsp.lang.token.Token;
import fsp.lang.token.Type;
import fsp.lang.type.Cons;
import fsp.lang.type.Symbol;
import java.math.BigInteger;

public class Parser {

  private final Scanner scanner;
  private boolean isComplete;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
    this.isComplete = false;
  }

  public Object parse() {

    if (scanner.peek().getType() == Type.EOF) {
      scanner.next();
      return null;
    }

    return program();
  }

  // program : expr
  public Object program() {

    return expr();
  }

  // expr : ε | SYMBOL | STRING | BOOLEAN | NUMBER | 'expr | list
  private Object expr() {
    Token token = scanner.peek();

    switch (token.getType()) {
      case SYMBOL:
        scanner.next();
        return new Symbol(token.getValue());
      case STRING:
        scanner.next();
        return token.getValue();
      case BOOLEAN:
        scanner.next();
        return Boolean.valueOf(token.getValue());
      case NUMBER:
        {
          scanner.next();
          String tokenValue = token.getValue();
          if (tokenValue.contains(".")) {
            return Float.parseFloat(tokenValue);
          }
          try {
            return Integer.parseInt(tokenValue);
          } catch (NumberFormatException ex) {
            return new BigInteger(tokenValue);
          }
        }
      case QUOTE:
        {
          scanner.next();
          Cons cons = new Cons();
          cons.setCar(new Symbol("quote"));
          cons.setCdr(new Cons().setCar(expr()));
          return cons;
        }
      case LPAREN:
        return list();
    }

    throw new SyntaxErrorException(scanner.peek().getValue());
  }

  // list : '(' expr* ')'
  private Object list() {
    Cons cons = new Cons();
    Cons prev = cons;
    Cons head = cons;
    if (scanner.peek().getType() != Type.LPAREN) {
      throw new SyntaxErrorException("syntax error - expected '('");
    }
    scanner.next();

    // Return null for empty list
    if (scanner.peek().getType() == Type.RPAREN) {
      scanner.next();
      return null;
    }

    while (scanner.peek().getType() != Type.RPAREN) {
      prev = cons;
      Cons next = new Cons();
      cons.setCar(expr());
      cons.setCdr(next);
      cons = next;
    }
    prev.setCdr(null);
    if (scanner.peek().getType() != Type.RPAREN) {
      throw new SyntaxErrorException("syntax error - expected ')'");
    }
    scanner.next();
    return head;
  }

  public boolean isComplete() {
    return isComplete;
  }
}
