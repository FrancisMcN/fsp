/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.exception;

public class UndefinedSymbolException extends RuntimeException {
  public UndefinedSymbolException(String msg) {
    super(String.format("symbol %s is undefined", msg));
  }
}
