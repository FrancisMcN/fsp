/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.token;

import java.util.Objects;

public class Token {

  private final Type type;
  private final String value;

  private int lineNo;

  public Token(Type type, String value, int lineNo) {
    this.type = type;
    this.value = value;
    this.lineNo = lineNo;
  }

  public Type getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public int getLineNo() {
    return lineNo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Token token = (Token) o;
    return lineNo == token.lineNo && type == token.type && Objects.equals(value, token.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value, lineNo);
  }

  @Override
  public String toString() {
    return String.format("(%s, '%s', %d)", type, value, lineNo);
  }
}
