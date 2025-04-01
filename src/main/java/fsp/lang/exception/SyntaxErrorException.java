/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.exception;

public class SyntaxErrorException extends RuntimeException {
  public SyntaxErrorException(String msg) {
    super(String.format("syntax error: %s", msg));
  }
}
