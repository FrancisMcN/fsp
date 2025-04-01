/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang;

import fsp.lang.exception.UndefinedSymbolException;
import fsp.lang.type.Symbol;
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

    // Check environment for symbol
    if (Environment.map.containsKey(name)) {
      return Environment.map.get(name);
    }
    // Check if symbol represents a loaded class
    try {
      return Class.forName(name.getValue());
    } catch (ClassNotFoundException ignored) {
    }

    // Throw an exception
    throw new UndefinedSymbolException(name.getValue());
  }

  public static boolean contains(Symbol symbol) {
    return Environment.map.containsKey(symbol);
  }
}
