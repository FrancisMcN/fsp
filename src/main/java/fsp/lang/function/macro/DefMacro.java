/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function.macro;

import fsp.lang.function.BaseLambda;
import fsp.lang.type.Cons;
import fsp.lang.type.Macro;
import fsp.lang.type.Symbol;

public class DefMacro extends BaseLambda implements Macro {
  @Override
  public Object call(Object arg, Object arg2, Object arg3) {

    return new Cons()
        .setCar(new Symbol("def"))
        .setCdr(
            new Cons()
                .setCar(arg)
                .setCdr(
                    new Cons()
                        .setCar(
                            new Cons()
                                .setCar(new Symbol("macro"))
                                .setCdr(new Cons().setCar(arg2).setCdr(new Cons().setCar(arg3))))));
  }
}
