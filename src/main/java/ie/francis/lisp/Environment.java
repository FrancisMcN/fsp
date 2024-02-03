/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp;

import ie.francis.lisp.type.Symbol;
import java.util.HashMap;
import java.util.Map;

public class Environment {

  private static final Map<Symbol, Object> map = new HashMap<>();

  public Environment() {}

  public static void put(Symbol name, Object value) {
    Environment.map.put(name, value);
  }

  public static Object get(Symbol name) {
    //        System.out.println( Environment.map.get(name));
    return Environment.map.get(name);
  }

  public static boolean contains(Symbol symbol) {
    return Environment.map.containsKey(symbol);
  }
}
