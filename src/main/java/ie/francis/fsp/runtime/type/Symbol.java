/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import static ie.francis.fsp.runtime.type.Type.SYMBOL;

import ie.francis.fsp.visitor.Visitor;
import java.util.Objects;

public class Symbol implements DataType {

  private final String value;

  public Symbol(String value) {
    this.value = value;
  }

  @Override
  public Type type() {
    return SYMBOL;
  }

  @Override
  public String name() {
    return this.value;
  }

  @Override
  public String descriptor() {
    return null;
  }

  @Override
  public String toString() {
    return this.value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Symbol symbol = (Symbol) o;
    return Objects.equals(value, symbol.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
