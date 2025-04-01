/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.exception;

public class UndefinedMacroException extends RuntimeException {
  public UndefinedMacroException(String msg) {
    super(String.format("undefined macro: %s", msg));
  }
}
