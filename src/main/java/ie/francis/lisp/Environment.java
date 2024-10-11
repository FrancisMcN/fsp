/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp;

import ie.francis.lisp.exception.UndefinedSymbolException;
import ie.francis.lisp.type.Symbol;
import java.util.HashMap;
import java.util.Map;

public class Environment {

  private static final Map<Symbol, Object> map = new HashMap<>();

  public Environment() {}

  public static Object put(Symbol name, Object value) {
    Environment.map.put(name, value);
    return null;
  }

  public static Object get(Symbol name) {
    if (Environment.map.containsKey(name)) {
      return Environment.map.get(name);
    }
    throw new UndefinedSymbolException(name.getValue());
  }

  public static boolean contains(Symbol symbol) {
    return Environment.map.containsKey(symbol);
  }
}
