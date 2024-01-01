/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp;

import ie.francis.lisp.function.Eval;
import ie.francis.lisp.function.Print;
import ie.francis.lisp.function.Read;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) throws IOException {

    String code = "((lambda (x  y) ()) 1 2)";

    if (args.length > 0) {
      String filename = args[0];
      String input = Files.readString(Path.of(filename));
      Read reader = new Read();
      Eval eval = new Eval();

      do {
        new Print().call(eval.call(reader.call(input)));
      } while (!reader.isComplete());
      return;
    }

    while (true) {
      BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("> ");
      String input;
      try {
        input = buffReader.readLine();

        if (input.equalsIgnoreCase("")) {
          continue;
        }
        if (input.equalsIgnoreCase("exit")) {
          break;
        }
        Read reader = new Read();
        Eval eval = new Eval();

        do {
          new Print().call(eval.call(reader.call(input)));
        } while (!reader.isComplete());

      } catch (IOException ignored) {
      }
    }
  }
}
