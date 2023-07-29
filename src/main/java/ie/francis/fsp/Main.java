/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) throws IOException {

    if (args.length > 0) {
      String filename = args[0];
      String data = Files.readString(Path.of(filename));
      new Runner().compileAndRun(data, true);
      return;
    }

    Repl repl = new Repl();
    while (true) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("> ");
      String input = reader.readLine();
      if (input.equalsIgnoreCase("exit")) {
        break;
      }
      repl.eval(input, true);
    }
  }
}
