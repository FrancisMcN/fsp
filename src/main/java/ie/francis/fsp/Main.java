/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.runtime.builtin.Read;
import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.runtime.type.Symbol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) throws IOException {

    Environment environment = new Environment();
    environment.loadBuiltins();

    if (args.length > 0) {
      String filename = args[0];
      String[] moduleNameParts = filename.replace("/", ".").split("\\.");
      String moduleName = moduleNameParts[moduleNameParts.length - 2];
      String data = Files.readString(Path.of(filename));
      Read.run(data);
      Symbol main = new Symbol(String.format("%s.main", moduleName));
      if (environment.contains(main)) {
        String mainClassName = ((DataType) environment.get(main)).name().replace(".run", "");
        Class<?> c = environment.loadClass(mainClassName);
        try {
          c.getMethod("run").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
      }
      return;
    }

    Repl repl = new Repl(environment);
    while (true) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("> ");
      String input = reader.readLine();
      if (input.equalsIgnoreCase("exit")) {
        break;
      }
      try {
        repl.eval(input, true);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
