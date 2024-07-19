/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Lambda;

public class List extends BaseLambda implements Lambda {
  @Override
  public Object call() {
    return new Cons();
  }

  @Override
  public Object call(Object arg) {
    return new Cons().setCar(arg);
  }

  @Override
  public Object call(Object arg, Object arg2) {
    return new Cons().setCar(arg).setCdr(new Cons().setCar(arg2));
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3) {
    return new Cons().setCar(arg).setCdr(new Cons().setCar(arg2).setCdr(new Cons().setCar(arg3)));
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4) {
    return new Cons()
        .setCar(arg)
        .setCdr(
            new Cons()
                .setCar(arg2)
                .setCdr(new Cons().setCar(arg3).setCdr(new Cons().setCar(arg4))));
  }

  @Override
  public Object call(Object arg, Object arg2, Object arg3, Object arg4, Object arg5) {
    return new Cons()
        .setCar(arg)
        .setCdr(
            new Cons()
                .setCar(arg2)
                .setCdr(
                    new Cons()
                        .setCar(arg3)
                        .setCdr(new Cons().setCar(arg4).setCdr(new Cons().setCar(arg5)))));
  }

  @Override
  public Object call(Object[] args) {
    Cons cons = new Cons();
    Cons initial = cons;
    for (Object arg : args) {
      cons.setCar(arg);
      cons.setCdr(new Cons());
      cons = cons.getCdr();
    }
    return initial;
  }
}
