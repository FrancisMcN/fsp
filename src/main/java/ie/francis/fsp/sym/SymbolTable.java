/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.sym;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  Map<String, Symbol> table;

  public SymbolTable() {
    table = new HashMap<>();
  }

  public Symbol get(String name) {
    return table.get(name);
  }

  public void put(String name, Symbol symbol) {
    table.put(name, symbol);
  }

  public boolean contains(String name) {
    return table.containsKey(name);
  }
}
