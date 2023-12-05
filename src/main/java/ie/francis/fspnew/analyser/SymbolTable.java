/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.analyser;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  Map<String, SymbolType> symbols;

  public SymbolTable() {
    this.symbols = new HashMap<>();
  }

  public void add(String symbol, SymbolType symbolType) {
    this.symbols.put(symbol, symbolType);
  }

  public SymbolType get(String symbol) {
    return symbols.get(symbol);
  }

  public boolean contains(String symbol) {
    return this.symbols.containsKey(symbol);
  }

  @Override
  public String toString() {
    return "SymbolTable{" + "symbols=" + symbols + '}';
  }
}
