/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function.macro;

import fsp.lang.function.BaseLambda;
import fsp.lang.type.Cons;
import fsp.lang.type.Macro;
import fsp.lang.type.Symbol;

public class Func extends BaseLambda implements Macro {
  @Override
  public Object call(Object arg, Object arg2, Object arg3) {
    //      Cons cons = (Cons) arg;
    //      Cons macro = (Cons) new Read().call("(def _ (lambda _ _))");
    //      // Replace first _ with function name
    //      macro.getCdr().setCar(cons.getCdr().getCar());
    //      // Replace second _ with function args
    //
    // ((Cons)(macro.getCdr().getCdr().getCar())).getCdr().setCar(cons.getCdr().getCdr().getCar());
    //      // Replace third _ with function body
    //
    // ((Cons)(macro.getCdr().getCdr().getCar())).getCdr().getCdr().setCar(cons.getCdr().getCdr().getCdr().getCar());
    //      return macro;

    return new Cons()
        .setCar(new Symbol("def"))
        .setCdr(
            new Cons()
                .setCar(arg)
                .setCdr(
                    new Cons()
                        .setCar(
                            new Cons()
                                .setCar(new Symbol("lambda"))
                                .setCdr(new Cons().setCar(arg2).setCdr(new Cons().setCar(arg3))))));
  }
}
