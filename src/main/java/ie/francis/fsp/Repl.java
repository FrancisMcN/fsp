/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.runtime.builtin.Read;
import java.io.IOException;

public class Repl {

  private final Environment environment;

  public Repl(Environment environment) {
    this.environment = environment;
  }

  public void eval(String value) throws IOException {
    eval(value, false);
  }

  public void eval(String value, boolean store) throws IOException {
    if (value.equalsIgnoreCase("")) {
      return;
    }
    Object output = Read.run(value);
    if (output != null) {
      System.out.println(output);
    }
  }
}
