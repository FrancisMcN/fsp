/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.exception;

public class TypeErrorException extends RuntimeException {
  public TypeErrorException(String msg) {
    super(String.format("type error: %s", msg));
  }
}
