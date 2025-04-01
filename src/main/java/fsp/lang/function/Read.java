/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.Buffer;
import fsp.lang.exception.NotImplementedException;
import fsp.lang.parser.Parser;
import fsp.lang.scanner.Scanner;
import fsp.lang.type.Lambda;

public class Read extends BaseLambda implements Lambda {

  private Scanner scanner;
  private Parser parser;

  @Override
  public Object call() {
    throw new NotImplementedException("method not implemented");
  }

  @Override
  public Object call(Object arg) {

    if (arg instanceof Buffer) {

      Buffer buff = (Buffer) arg;
      scanner = new Scanner(buff.data());
      parser = new Parser(scanner);
      Object object = parser.parse();
      buff.advance(scanner.getPtr());
      return object;

    } else {

      String input = (String) arg;
      scanner = new Scanner(input);
      parser = new Parser(scanner);
      return parser.parse();
    }
  }

  @Override
  public Object call(Object[] args) {
    throw new NotImplementedException("method not implemented");
  }

  public boolean isComplete() {
    return parser.isComplete();
  }
}
