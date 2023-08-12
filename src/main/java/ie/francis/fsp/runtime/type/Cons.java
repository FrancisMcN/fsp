/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import ie.francis.fsp.visitor.Visitor;

import static ie.francis.fsp.runtime.type.Type.CONS;

public class Cons implements DataType {

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
  public Type type() {
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

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
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

  public int size() {
    int size = 1;
    Cons temp = cdr;
    while (temp != null) {
      size++;
      temp = temp.getCdr();
    }
    return size;
  }
}
