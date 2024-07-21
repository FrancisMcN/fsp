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

    return program();

    //    Object object = null;
    //    if (scanner.hasNext()) {
    //      object = expr();
    //    }
    //
    //    Cons cons = new Cons();
    //    cons.setCar(new Symbol("progn"));
    //    cons.setCdr(new Cons().setCar(object));
    //    cons = cons.getCdr();
    //    while (scanner.hasNext()) {
    //      cons.setCdr(new Cons());
    //      cons = cons.getCdr();
    //      cons.setCar(expr());
    //    }
    //
    //    return cons;

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
          return Integer.parseInt(tokenValue);
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
