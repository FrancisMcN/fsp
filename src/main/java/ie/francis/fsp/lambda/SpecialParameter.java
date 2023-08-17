/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.lambda;

import static ie.francis.fsp.runtime.type.Type.SYMBOL;

import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.runtime.type.Symbol;

public class SpecialParameter {

  public static boolean isSpecialParameter(DataType data) {
    if (data.type() == SYMBOL) {
      return isSpecialParameter((Symbol) data);
    }
    return false;
  }

  public static boolean isSpecialParameter(Symbol parameter) {
    switch (parameter.name()) {
      case "&rest":
      case "&optional":
      case "&key":
        return true;
      default:
        return false;
    }
  }
}
