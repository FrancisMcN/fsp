/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.scanner;

import ie.francis.lisp.token.Token;
import ie.francis.lisp.token.Type;

public class Scanner {

  private final String input;
  private int ptr;

  private int lineNo;

  public Scanner(String input) {
    this.input = input;
    this.ptr = 0;
    this.lineNo = 1;
  }

  public int getPtr() {
    return ptr;
  }

  public boolean hasNext() {
    return peek().getType() != Type.EOF;
  }

  public Token peek() {
    int ptr = this.ptr;
    int lineNo = this.lineNo;
    Token token = next();
    this.ptr = ptr;
    this.lineNo = lineNo;
    return token;
  }

  public Token next() {
    while (ptr < input.length()) {
      char c = input.charAt(ptr);
      switch (c) {
        case '\n':
          ptr++;
          lineNo++;
          continue;
        case '(':
          ptr++;
          return new Token(Type.LPAREN, "(", lineNo);
        case ')':
          ptr++;
          return new Token(Type.RPAREN, ")", lineNo);
        case '#':
          ptr++;
          return new Token(Type.HASH, "#", lineNo);
        case '.':
          ptr++;
          return new Token(Type.DOT, ".", lineNo);
        case '"':
          ptr++;
          return stringToken();
        case ';':
          ptr++;
          ignoreComment();
          break;
        case '\'':
          ptr++;
          return new Token(Type.QUOTE, "'", lineNo);
        default:
          {
            if (Character.isDigit(c)) {
              return numberToken();
            } else if (Character.isWhitespace(c)) {
              ptr++;
            } else {
              return symbolToken();
            }
          }
      }
    }
    return new Token(Type.EOF, "eof", lineNo);
  }

  private boolean isSpecial(char character) {
    switch (character) {
      case '(':
      case ')':
      case '\'':
      case '#':
      case '`':
      case '"':
        return true;
      default:
        return false;
    }
  }

  private void ignoreComment() {
    while (ptr < input.length() && input.charAt(ptr) != '\n') {
      ptr++;
    }
  }

  private Token stringToken() {
    StringBuilder sb = new StringBuilder();
    while (ptr < input.length() && input.charAt(ptr) != '"') {
      sb.append(input.charAt(ptr++));
    }
    ptr++;
    return new Token(Type.STRING, sb.toString(), lineNo);
  }

  private Token numberToken() {
    StringBuilder sb = new StringBuilder();
    while (ptr < input.length()
        && (Character.isDigit(input.charAt(ptr)) || input.charAt(ptr) == '.')) {
      sb.append(input.charAt(ptr++));
    }
    return new Token(Type.NUMBER, sb.toString(), lineNo);
  }

  private Token symbolToken() {
    StringBuilder sb = new StringBuilder();
    while (ptr < input.length() && !isSpecialOrWhitespace(input.charAt(ptr))) {
      sb.append(input.charAt(ptr++));
    }
    if (sb.toString().equals("true") || sb.toString().equals("false")) {
      return new Token(Type.BOOLEAN, sb.toString(), lineNo);
    }
    return new Token(Type.SYMBOL, sb.toString(), lineNo);
  }

  boolean isSpecialOrWhitespace(char character) {
    if (isSpecial(character)) {
      return true;
    }
    return Character.isWhitespace(character);
  }
}
