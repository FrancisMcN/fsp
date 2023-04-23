/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import ie.francis.fsp.ast.Node;
import ie.francis.fsp.ast.Visitor;
import ie.francis.fsp.ast.VisitorImpl;
import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.scanner.Scanner;

public class Repl {
  public void eval(String value) {
    try {
      Scanner scanner = new Scanner(value);
      Parser parser = new Parser(scanner);
      Node ast = parser.parse();
      Visitor visitor = new VisitorImpl();
      ast.accept(visitor);
    } catch (SyntaxErrorException ex) {
      ex.printStackTrace();
    }

    //        while (scanner.hasNext()) {
    //            System.out.println(scanner.next());
    //        }
  }
}
