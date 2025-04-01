/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.Environment;
import fsp.lang.exception.UndefinedMacroException;
import fsp.lang.type.Cons;
import fsp.lang.type.Lambda;
import fsp.lang.type.Macro;
import fsp.lang.type.Symbol;

public class MacroExpand1 extends BaseLambda implements Lambda {

  public Object call(Object object) {
    Cons cons = (Cons) object;
    int arguments = cons.size() - 1;
    Symbol name = (Symbol) cons.getCar();
    if (Environment.contains(name)) {
      Macro macro = (Macro) Environment.get(name);
      switch (arguments) {
        case 0:
          return macro.call();
        case 1:
          return macro.call(cons.getCdr().getCar());
        case 2:
          return macro.call(cons.getCdr().getCar(), cons.getCdr().getCdr().getCar());
        case 3:
          return macro.call(
              cons.getCdr().getCar(),
              cons.getCdr().getCdr().getCar(),
              cons.getCdr().getCdr().getCdr().getCar());
        case 4:
          return macro.call(
              cons.getCdr().getCar(),
              cons.getCdr().getCdr().getCar(),
              cons.getCdr().getCdr().getCdr().getCar(),
              cons.getCdr().getCdr().getCdr().getCdr().getCar());
        case 5:
          return macro.call(
              cons.getCdr().getCar(),
              cons.getCdr().getCdr().getCar(),
              cons.getCdr().getCdr().getCdr().getCar(),
              cons.getCdr().getCdr().getCdr().getCdr().getCar(),
              cons.getCdr().getCdr().getCdr().getCdr().getCdr().getCar());
        default:
          {
            Object[] objects = new Object[arguments];
            for (int i = 0; i < arguments; i++) {
              objects[i] = cons.getCar();
              cons = cons.getCdr();
            }
            return macro.call(objects);
          }
      }
    }
    throw new UndefinedMacroException(name.getValue());
  }

  @Override
  public Object call(Object[] args) {
    //    if (arg instanceof Cons) {
    Symbol name = (Symbol) args[0];
    if (Environment.contains(name)) {
      Macro macro = (Macro) Environment.get(name);
      return macro.call(args);
    }
    throw new UndefinedMacroException(name.getValue());
    //    }
    //    return arg;
  }
}
