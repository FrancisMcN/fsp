/*
 * (c) 2025 Francis McNamee
 * */
 
package ie.francis.lisp;

import ie.francis.lisp.function.*;
import ie.francis.lisp.function.macro.Func;
import ie.francis.lisp.type.Symbol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

  public static void run(String[] args) throws IOException {
    Environment.put(new Symbol("nil"), null);
    Environment.put(new Symbol("apply"), new Apply());
    Environment.put(new Symbol("car"), new Car());
    Environment.put(new Symbol("cdr"), new Cdr());
    Environment.put(new Symbol("compile"), new Compile());
    Environment.put(new Symbol("eval"), new Eval());
    Environment.put(new Symbol("print"), new Print());
    Environment.put(new Symbol("read"), new Read());
    Environment.put(new Symbol("type"), new Type());
    Environment.put(new Symbol("list"), new List());
    Environment.put(new Symbol("macroexpand-1"), new MacroExpand1());
    Environment.put(new Symbol("func"), new Func());
    Environment.put(new Symbol("+"), new Plus());
    Environment.put(new Symbol("-"), new Minus());
    Environment.put(new Symbol("="), new Equal());
    Environment.put(new Symbol("<"), new LessThan());
    Environment.put(new Symbol(">"), new GreaterThan());

    if (args.length > 0 && !args[0].startsWith("-")) {
      String filename = args[0];
      String input = Files.readString(Path.of(filename));
      Read reader = new Read();
      Eval eval = new Eval();

      Buffer buff = new Buffer(input);
      Object result = null;
      while (!buff.complete()) {
        Object temp = reader.call(buff);
        if (temp != null) {
          result = eval.call(temp);
        }
      }
      new Print().call(result);

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
        Buffer buff = new Buffer(input);
        while (!buff.complete()) {
          Object temp = reader.call(buff);
          if (temp != null) {
            new Print().call(eval.call(temp));
          }
        }

      } catch (RuntimeException exception) {
        exception.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws IOException {

    try {

      CommandLineParser commandLineParser = new DefaultParser();
      Options options = new Options();
      Option versionOption = new Option("version", false, "print fsp version");
      Option helpOption = new Option("help", false, "print helpful information for using the CLI");
      Option debugOption =
          new Option("debug", false, "write generated class files to disk to aid debugging");

      options.addOption(helpOption);
      options.addOption(debugOption);
      options.addOption(versionOption);

      CommandLine commandLine = commandLineParser.parse(options, args);

      if (commandLine.hasOption("debug")) {
        Compile.writeToDisk = true;
      }

      if (commandLine.hasOption("version")) {
        System.out.printf("Lisp %s%n", Version.VERSION_STRING);
      } else if (commandLine.hasOption("help")) {
        HelpFormatter fmt = new HelpFormatter();
        fmt.printHelp("-help", options);
      } else {
        run(args);
      }

    } catch (ParseException ex) {
      System.err.println(ex.getMessage());
      System.err.println("Run 'fsp -help' for usage.");
      System.exit(1);
    }
  }
}
