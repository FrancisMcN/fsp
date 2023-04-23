/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.scanner;

import ie.francis.fsp.token.Token;
import ie.francis.fsp.token.Type;

public class Scanner {

  private final String input;
  private int ptr;

  public Scanner(String input) {
    this.input = input;
    this.ptr = 0;
  }

  public boolean hasNext() {
    return ptr < input.length();
  }

  public Token peek() {
    int ptr = this.ptr;
    Token token = next();
    this.ptr = ptr;
    return token;
  }

  public Token next() {
    while (ptr < input.length()) {
      char c = input.charAt(ptr);
      switch (c) {
        case '(':
          ptr++;
          return new Token(Type.LPAREN, "(");
        case ')':
          ptr++;
          return new Token(Type.RPAREN, ")");
        case '#':
          ptr++;
          return new Token(Type.HASH, "#");
        case '\'':
          ptr++;
          return new Token(Type.QUOTE, "'");
        case '`':
          ptr++;
          return new Token(Type.TICK, "`");
        case '"':
          ptr++;
          return stringToken();
        case ';':
          ptr++;
          ignoreComment();
          break;
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
    return new Token(Type.EOF, "eof");
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
    return new Token(Type.STRING, sb.toString());
  }

  private Token numberToken() {
    StringBuilder sb = new StringBuilder();
    while (ptr < input.length()
        && (Character.isDigit(input.charAt(ptr)) || input.charAt(ptr) == '.')) {
      sb.append(input.charAt(ptr++));
    }
    return new Token(Type.NUMBER, sb.toString());
  }

  private Token symbolToken() {
    StringBuilder sb = new StringBuilder();
    while (ptr < input.length() && !isSpecialOrWhitespace(input.charAt(ptr))) {
      sb.append(input.charAt(ptr++));
    }
    return new Token(Type.SYMBOL, sb.toString());
  }

  private boolean isSpecialOrWhitespace(char character) {
    switch (character) {
      case '(':
      case ')':
      case '\'':
      case '#':
      case '`':
      case '"':
        return true;
      default:
        return Character.isWhitespace(character);
    }
  }
}
