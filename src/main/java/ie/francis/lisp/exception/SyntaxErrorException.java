/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.exception;

public class SyntaxErrorException extends RuntimeException {
  public SyntaxErrorException(String msg) {
    super(String.format("syntax error: %s", msg));
  }
}
