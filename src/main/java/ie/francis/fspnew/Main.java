/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew;

import ie.francis.fspnew.repl.Repl;
import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      // Repl
      Repl repl = new Repl();
      repl.loop();
    }
  }
}
