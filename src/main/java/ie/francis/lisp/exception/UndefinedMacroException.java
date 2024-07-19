/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.exception;

public class UndefinedMacroException extends RuntimeException {
  public UndefinedMacroException(String msg) {
    super(String.format("undefined macro: %s", msg));
  }
}
