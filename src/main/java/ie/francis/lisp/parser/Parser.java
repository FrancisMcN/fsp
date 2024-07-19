/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.parser;

import ie.francis.lisp.exception.SyntaxErrorException;
import ie.francis.lisp.scanner.Scanner;
import ie.francis.lisp.token.Token;
import ie.francis.lisp.token.Type;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Symbol;

public class Parser {

  private final Scanner scanner;
  private boolean isComplete;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
    this.isComplete = false;
  }

  public Object parse() {
    Object expr = expr();
    if (!scanner.hasNext()) {
      isComplete = true;
    }
    return expr;
  }

  public boolean isComplete() {
    return isComplete;
  }

  // expr : ε | SYMBOL | STRING | BOOLEAN | NUMBER | list | 'expr
  private Object expr() {
    Token token = scanner.peek();

    switch (token.getType()) {
      case QUOTE:
        {
          scanner.next();
          Cons cons = new Cons();
          cons.setCar(new Symbol("quote"));
          cons.setCdr(new Cons().setCar(expr()));
          return cons;
        }
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
          return Integer.parseInt(tokenValue);
        }
      case LPAREN:
        return list();
    }
    System.out.println(scanner);
    throw new SyntaxErrorException("syntax error");
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
}
