/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args) throws IOException {

    Repl repl = new Repl();
    while (true) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("> ");
      String input = reader.readLine();
      if (input.equalsIgnoreCase("exit")) {
        break;
      }
      if (args.length > 0) {
        repl.eval(input, true);
      } else {
        repl.eval(input);
      }
    }
  }
}
