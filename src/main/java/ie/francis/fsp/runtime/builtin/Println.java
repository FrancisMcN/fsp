/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

public class Println {

  public static Object run(Object value) {
    System.out.println(value);
    return null;
  }
}
