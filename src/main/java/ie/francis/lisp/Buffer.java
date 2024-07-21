/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp;

public class Buffer {

  private final String buff;
  private int ptr;

  public Buffer(String buff) {
    this.buff = buff;
    this.ptr = 0;
  }

  public Buffer advance(int ptr) {
    this.ptr += ptr;
    return this;
  }

  public boolean complete() {
    return buff.length() == ptr;
  }

  public String data() {
    return buff.substring(ptr);
  }
}
