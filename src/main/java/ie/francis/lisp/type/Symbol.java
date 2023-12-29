/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.type;

public class Symbol {
  private final String value;

  public Symbol(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
