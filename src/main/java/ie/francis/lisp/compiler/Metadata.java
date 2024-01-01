/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.compiler;

public class Metadata {

  public static enum Type {
    INTEGER,
    FLOAT,
    STRING,
    SYMBOL,
    BOOLEAN,
    CONS,
    LAMBDA,
    NIL
  }

  private final Type type;
  private final String value;

  public Metadata(Type type, String value) {
    this.type = type;
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "Meta{" + "type=" + type + ", value='" + value + '\'' + '}';
  }
}
