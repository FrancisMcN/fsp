/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import static ie.francis.fsp.runtime.type.DataType.CONS;

public class Cons implements Type {

  private Object car;
  private Cons cdr;

  public Cons() {}

  public void setCar(Object car) {
    this.car = car;
  }

  public Object getCar() {
    return car;
  }

  public void setCdr(Cons cdr) {
    this.cdr = cdr;
  }

  public Cons getCdr() {
    return cdr;
  }

  @Override
  public DataType type() {
    return CONS;
  }

  @Override
  public String name() {
    return "cons";
  }

  @Override
  public String descriptor() {
    return "";
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Cons temp = this;
    sb.append("(");
    while (temp != null) {
      sb.append(str(temp.getCar()));
      temp = temp.getCdr();
      if (temp != null) {
        sb.append(" ");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  public String str(Object object) {
    String objectString;
    if (object == null) {
      objectString = "nil";
    } else {
      objectString = object.toString();
    }
    return objectString;
  }
}
