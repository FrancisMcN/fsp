/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.type;

import java.util.Objects;

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

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Symbol) {
      return ((Symbol) obj).getValue().equals(this.getValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
