/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.sym;

public interface Symbol {
  String name();

  String descriptor();

  SymbolType type();
}
