/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.type;

public class Cons {

  private Object car;
  private Cons cdr;

  public Cons(Object car, Cons cdr) {
    this.car = car;
    this.cdr = cdr;
  }

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

  public int size() {
    int size = 1;
    Cons cons = getCdr();
    while (cons != null) {
      size++;
      cons = cons.getCdr();
    }
    return size;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Cons temp = this;
    sb.append("(");
    while (temp != null) {
      if (temp.getCar() == null) {
        sb.append("nil");
      } else {
        sb.append(temp.getCar());
      }
      temp = temp.getCdr();
      if (temp != null) {
        sb.append(" ");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
