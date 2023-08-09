/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.helper;

import ie.francis.fsp.runtime.type.Cons;

public class ConsBuilder {

  private final Cons cons;
  private Cons ptr;

  public ConsBuilder() {
    this.cons = new Cons();
    this.ptr = cons;
  }

  public void add(Object object) {
    if (ptr.getCar() == null) {
      ptr.setCar(object);
    } else {
      Cons cons2 = new Cons();
      cons2.setCar(object);
      ptr.setCdr(cons2);
      ptr = ptr.getCdr();
    }
  }

  public Cons getCons() {
    return cons;
  }
}
