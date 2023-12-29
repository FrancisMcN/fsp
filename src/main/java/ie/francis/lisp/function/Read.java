/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.exception.NotImplementedException;
import ie.francis.lisp.parser.Parser;
import ie.francis.lisp.scanner.Scanner;

public class Read implements Lambda {
  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {
    String input = (String) arg;
    Scanner scanner = new Scanner(input);
    Parser parser = new Parser(scanner);
    return parser.parse();
  }
}
