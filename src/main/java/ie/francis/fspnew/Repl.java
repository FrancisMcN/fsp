/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew;

import ie.francis.fspnew.compiler.Compiler;
import ie.francis.fspnew.compiler.JavaBytecodeGenerator;
import ie.francis.fspnew.exception.SyntaxErrorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Repl {

  public Repl() {}

  public void loop() throws IOException {
    while (true) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("> ");
      String input = reader.readLine();
      if (input.equalsIgnoreCase("exit")) {
        break;
      }
      Compiler compiler = new Compiler(new JavaBytecodeGenerator());
      try {
        compiler.compile(input);
      } catch (SyntaxErrorException ex) {
        ex.printStackTrace();
      }
    }
  }
}
