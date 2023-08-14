/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.runtime.type.Function;
import ie.francis.fsp.scanner.Scanner;
import ie.francis.fsp.visitor.Acceptor;
import ie.francis.fsp.visitor.AcceptorImpl;
import ie.francis.fsp.visitor.ClassGeneratorVisitor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Read extends Function {
  public Read() {
    super(
        String.format("%s.run", Read.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("value")));
  }

  static int runs = 0;

  static Acceptor acceptor = new AcceptorImpl();

  public static Object run(Object value) {
    Environment environment = Environment.singleton();
    Scanner scanner = new Scanner((String) value);
    Parser parser = new Parser(scanner);
    List<DataType> prog = parser.parse();
    ClassGeneratorVisitor compileVisitor =
        new ClassGeneratorVisitor("Test" + String.valueOf(runs), new ArrayList<>(), environment);
    for (DataType expression : prog) {
      acceptor.accept(expression, compileVisitor);
    }
    byte[] bytes = compileVisitor.generate();
    Object o = null;
    try {
      compileVisitor.write();

      Class<?> c = environment.loadClass(compileVisitor.getClassName(), bytes);
      o = c.getMethod("run").invoke(null);
    } catch (SyntaxErrorException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException ex) {
      ex.printStackTrace();
    }
    return o;
  }
}
