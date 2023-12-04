/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.exception;

public class SyntaxErrorException extends RuntimeException {
  public SyntaxErrorException(String msg) {
    super(String.format("syntax error: %s", msg));
  }
}
