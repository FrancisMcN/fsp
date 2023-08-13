/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.token;

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
  public String toString() {
    return String.format("(%s, '%s', %d)", type, value, lineNo);
  }
}
