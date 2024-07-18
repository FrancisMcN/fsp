/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp;

import ie.francis.lisp.function.*;
import ie.francis.lisp.type.Symbol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) throws IOException {

    String code = "((lambda (x  y) ()) 1 2)";

    Environment.put(new Symbol("apply"), new Apply());
    Environment.put(new Symbol("car"), new Car());
    Environment.put(new Symbol("cdr"), new Cdr());
    Environment.put(new Symbol("compile"), new Compile());
    Environment.put(new Symbol("eval"), new Eval());
    Environment.put(new Symbol("print"), new Print());
    Environment.put(new Symbol("read"), new Read());
    Environment.put(new Symbol("type"), new Type());
    Environment.put(new Symbol("+"), new Plus());
    Environment.put(new Symbol("-"), new Minus());
    Environment.put(new Symbol("="), new Equal());
    Environment.put(new Symbol("<"), new LessThan());
    Environment.put(new Symbol(">"), new GreaterThan());

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

    System.out.printf("Lisp %s%n", Version.VERSION_STRING);
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

      } catch (RuntimeException exception) {
        exception.printStackTrace();
      }
    }
  }
}
