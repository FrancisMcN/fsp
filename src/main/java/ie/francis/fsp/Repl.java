/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

public class Repl {

  private final Runner runner;

  public Repl() {
    this.runner = new Runner();
  }

  public void eval(String value) {
    eval(value, false);
  }

  public void eval(String value, boolean store) {
    if (value.equalsIgnoreCase("")) {
      return;
    }
    runner.compileAndRun(value, true);
  }
}
