/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.parser;

import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.runtime.rmacro.DerefReaderMacro;
import ie.francis.fsp.runtime.rmacro.QuoteReaderMacro;
import ie.francis.fsp.runtime.rmacro.ReaderMacro;
import ie.francis.fsp.runtime.type.Atom;
import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.runtime.type.Symbol;
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

  // prog        : rmacro_expr
  // rmacro_expr : RMACRO? rmacro_expr | expr
  // expr        : SYMBOL | STRING | NUMBER | BOOLEAN | list
  // list        : '(' seq ')'
  // seq         : rmacro_expr*
  public List<DataType> parse() throws SyntaxErrorException {
    List<DataType> exprs = new ArrayList<>();
    while (scanner.hasNext()) {
      exprs.add(rmacro_expr());
    }
    return exprs;
  }

  protected DataType rmacro_expr() throws SyntaxErrorException {
    if (scanner.peek().getType() == Type.RMACRO) {
      Token token = scanner.peek();
      scanner.next();
      return ((DataType) readerMacros.get(token.getValue()).expand(rmacro_expr()));
    }
    return expr();
  }

  // expr : SYMBOL | STRING | NUMBER | BOOLEAN | list
  protected DataType expr() throws SyntaxErrorException {
    Token token = scanner.peek();
    switch (token.getType()) {
      case SYMBOL:
        {
          scanner.next();
          return new Atom(new Symbol(token.getValue()));
        }
      case STRING:
        {
          scanner.next();
          return new Atom(token.getValue());
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
      case BOOLEAN:
        {
          scanner.next();
          return new Atom(Boolean.parseBoolean(token.getValue()));
        }
      case LPAREN:
        return list();
      default:
        System.out.println(scanner.peek());
        throw new SyntaxErrorException("syntax error");
    }
  }

  // list : '(' seq ')'
  protected DataType list() throws SyntaxErrorException {
    scanner.next();

    DataType seq = seq();

    Token token = scanner.peek();
    if (token.getType() != Type.RPAREN) {
      throw new SyntaxErrorException("expected ')', found: " + token);
    }
    scanner.next();

    return seq;
  }

  // seq : expr*
  protected DataType seq() throws SyntaxErrorException {
    Token token = scanner.peek();

    ConsBuilder consBuilder = new ConsBuilder();
    while (token.getType() == Type.SYMBOL
        || token.getType() == Type.STRING
        || token.getType() == Type.NUMBER
        || token.getType() == Type.BOOLEAN
        || token.getType() == Type.LPAREN
        || token.getType() == Type.RMACRO) {
      consBuilder.add(rmacro_expr());
      token = scanner.peek();
    }
    return consBuilder.getCons();
  }

  protected Map<String, ReaderMacro> getReaderMacros() {
    return readerMacros;
  }
}
