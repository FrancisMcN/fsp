/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

public class Repl {

  public void eval(String value) {
    eval(value, false);
  }

  public void eval(String value, boolean store) {
    if (value.equalsIgnoreCase("")) {
      return;
    }
    new Runner().compileAndRun(value, true);
  }
}
