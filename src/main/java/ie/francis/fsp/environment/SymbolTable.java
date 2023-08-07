/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  Map<String, Entry> table;

  public SymbolTable() {
    table = new HashMap<>();
  }

  public Entry get(String name) {
    return table.get(name);
  }

  public void put(String name, Entry symbol) {
    table.put(name, symbol);
  }

  public boolean contains(String name) {
    return table.containsKey(name);
  }
}
