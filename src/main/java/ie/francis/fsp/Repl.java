/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import ie.francis.fsp.ast.Node;
import ie.francis.fsp.classloader.CustomClassLoader;
import ie.francis.fsp.codegen.ClassGeneratorVisitor;
import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.scanner.Scanner;
import java.io.FileOutputStream;
import java.io.IOException;
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
      ClassGeneratorVisitor visitor = new ClassGeneratorVisitor("Test");
      ast.accept(visitor);
      byte[] bytes = visitor.generate();
      try (FileOutputStream fos = new FileOutputStream("Test.class")) {
        fos.write(bytes);
      }

      Class c = new CustomClassLoader().defineClass("ie.francis.Test", bytes);
      c.getMethod("main").invoke(null);
    } catch (SyntaxErrorException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException
        | IOException ex) {
      ex.printStackTrace();
    }
  }
}
