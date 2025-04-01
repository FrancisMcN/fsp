/*
 * (c) 2025 Francis McNamee
 * */
 
package ie.francis.lisp.exception;

public class UndefinedSymbolException extends RuntimeException {
  public UndefinedSymbolException(String msg) {
    super(String.format("symbol %s is undefined", msg));
  }
}
