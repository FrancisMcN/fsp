/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.exception;

public class NotImplementedException extends RuntimeException {
  public NotImplementedException(String method) {
    super(String.format("%s is not implemented", method));
  }
}
