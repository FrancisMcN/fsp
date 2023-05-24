/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import ie.francis.fsp.ast.Node;
import ie.francis.fsp.ast.Visitor;
import ie.francis.fsp.ast.VisitorImpl;
import ie.francis.fsp.classloader.CustomClassLoader;
import ie.francis.fsp.codegen.ClassGenerator;
import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.scanner.Scanner;
import java.lang.reflect.InvocationTargetException;

public class Repl {
  public void eval(String value) {
    try {
      if (value.equalsIgnoreCase("")) {
        return;
      }
      Scanner scanner = new Scanner(value);
      Parser parser = new Parser(scanner);
      Node ast = parser.parse();
      Visitor visitor = new VisitorImpl();
      ast.accept(visitor);
      ClassGenerator generator = new ClassGenerator("testclass");
      byte[] bytes = generator.generate();
      //      try (FileOutputStream fos = new FileOutputStream("test.class")) {
      //        fos.write(bytes);
      //      }

      //      System.out.println(Arrays.toString(bytes));
      Class c = new CustomClassLoader().defineClass("ie.francis.testclass", bytes);
      c.getMethod("main").invoke(null);
    } catch (SyntaxErrorException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException ex) {
      ex.printStackTrace();
    }
  }
}
