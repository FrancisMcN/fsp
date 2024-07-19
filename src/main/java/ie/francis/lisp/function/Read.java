/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.exception.NotImplementedException;
import ie.francis.lisp.parser.Parser;
import ie.francis.lisp.scanner.Scanner;
import ie.francis.lisp.type.Lambda;

public class Read extends BaseLambda implements Lambda {

  private Scanner scanner;
  private Parser parser;

  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {
    String input = (String) arg;
    scanner = new Scanner(input);
    parser = new Parser(scanner);
    return parser.parse();
  }

  @Override
  public Object call(Object[] args) {
    throw new NotImplementedException("method not implemented");
  }

  public boolean isComplete() {
    return parser.isComplete();
  }
}
