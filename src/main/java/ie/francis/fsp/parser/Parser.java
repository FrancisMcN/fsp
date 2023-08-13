/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.parser;

import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.runtime.rmacro.DerefReaderMacro;
import ie.francis.fsp.runtime.rmacro.QuoteReaderMacro;
import ie.francis.fsp.runtime.rmacro.ReaderMacro;
import ie.francis.fsp.runtime.type.*;
import ie.francis.fsp.scanner.Scanner;
import ie.francis.fsp.token.Token;
import ie.francis.fsp.token.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
  private final Scanner scanner;
  private final Map<String, ReaderMacro> readerMacros;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
    this.readerMacros = new HashMap<>();
    ReaderMacro quoteReaderMacro = new QuoteReaderMacro();
    ReaderMacro derefReaderMacro = new DerefReaderMacro();
    this.readerMacros.put(quoteReaderMacro.character(), quoteReaderMacro);
    this.readerMacros.put(derefReaderMacro.character(), derefReaderMacro);
  }

  // prog : sxpr*
  // sxpr : atom | list
  // list : '(' sxpr+ ['.' sxpr]? ')'
  // atom : SYMBOL | NUMBER | STRING | BOOLEAN | ε

  public List<DataType> parse() throws SyntaxErrorException {
    List<DataType> expressions = new ArrayList<>();
    while (scanner.peek().getType() != Type.EOF) {
      expressions.add(sxpr());
    }
    return expressions;
  }

  // sxpr : ( RMACRO sxpr) | atom | '(' sxpr '.' sxpr ')' | list
  public DataType sxpr() throws SyntaxErrorException {
    Token token = scanner.peek();

    if (token.getType() == Type.RMACRO) {
      scanner.next();
      return (DataType) readerMacros.get(token.getValue()).expand(sxpr());
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

  // list : '(' sxpr* ['.' sxpr]? ')'
  public DataType list() {
    ConsBuilder consBuilder = new ConsBuilder();
    scanner.next();
    while (scanner.peek().getType() != Type.RPAREN && scanner.peek().getType() != Type.DOT) {
      consBuilder.add(sxpr());
    }

    Token token = scanner.peek();
    //        if (token.getType() == Type.DOT) {
    //            scanner.next();
    //            SxprNode sxprNode = new SxprNode();
    //            sxprNode.setCar(list.getNodes().remove(list.getNodes().size() - 1));
    //            sxprNode.setCdr(sxpr());
    //            list.addNode(sxprNode);
    //        }

    if (scanner.peek().getType() != Type.RPAREN) {
      throw new SyntaxErrorException(String.format("expected ')', found: %s", token.getType()));
    }

    scanner.next();
    return consBuilder.getCons();
  }

  // atom : SYMBOL | NUMBER | STRING | BOOLEAN | ε
  private DataType atom() throws SyntaxErrorException {
    Token token = scanner.peek();
    switch (token.getType()) {
      case SYMBOL:
        {
          scanner.next();
          return new Atom(new Symbol(token.getValue()));
        }
      case NUMBER:
        {
          scanner.next();
          String tokenValue = token.getValue();
          if (tokenValue.contains(".")) {
            return new Atom(Float.parseFloat(tokenValue));
          }
          return new Atom(Integer.parseInt(tokenValue));
        }
      case STRING:
        {
          scanner.next();
          return new Atom(token.getValue());
        }
      case BOOLEAN:
        {
          scanner.next();
          return new Atom(Boolean.parseBoolean(token.getValue()));
        }
      default:
        {
          throw new SyntaxErrorException(
              String.format("expected symbol, number, string or list. Found: %s", token.getType()));
        }
    }
  }
}
