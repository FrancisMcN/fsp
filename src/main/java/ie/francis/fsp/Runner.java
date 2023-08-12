/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.exception.SyntaxErrorException;
//import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.scanner.Scanner;
//import ie.francis.fsp.visitor.ClassGeneratorVisitor;
import ie.francis.fsp.visitor.ClassGeneratorVisitor;
//import ie.francis.fsp.visitor.MacroExpanderVisitor;
//import ie.francis.fsp.visitor.StringVisitor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Runner {

  private final Environment environment;
  private int runs;

  public Runner() {
    this.environment = new Environment();
    this.environment.loadBuiltins();
    this.runs = 0;
  }

  public void compileAndRun(String data, boolean store) {
    Scanner scanner = new Scanner(data);
    Parser parser = new Parser(scanner);
    List<DataType> prog = parser.parse();
    ClassGeneratorVisitor compileVisitor =
            new ClassGeneratorVisitor(
                    "Test" + String.valueOf(this.runs), new ArrayList<>(), environment);
    for (DataType expression : prog) {
      expression.accept(compileVisitor);
    }
    byte[] bytes = compileVisitor.generate();
    try {
      if (store) {
        compileVisitor.write();
      }

      Class<?> c = environment.loadClass(compileVisitor.getClassName(), bytes);
      Object o = c.getMethod("run").invoke(null);
      if (o != null) {
        System.out.println(o);
      }
    } catch (SyntaxErrorException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException ex) {
      ex.printStackTrace();
    }
    runs++;
  }
}
