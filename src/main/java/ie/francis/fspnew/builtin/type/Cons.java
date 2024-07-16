/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.builtin.type;

public class Cons {

  private Object car;
  private Cons cdr;

  public Cons() {}

  public Cons setCar(Object car) {
    this.car = car;
    return this;
  }

  public Object getCar() {
    return car;
  }

  public Cons setCdr(Cons cdr) {
    this.cdr = cdr;
    return this;
  }

  public Cons getCdr() {
    return cdr;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Cons temp = this;
    sb.append("(");
    while (temp != null) {
      sb.append(temp.getCar());
      temp = temp.getCdr();
      if (temp != null) {
        sb.append(" ");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
