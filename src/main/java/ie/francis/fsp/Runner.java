/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp;

import ie.francis.fsp.environment.Environment;
// import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.runtime.builtin.Read;
// import ie.francis.fsp.visitor.ClassGeneratorVisitor;
// import ie.francis.fsp.visitor.MacroExpanderVisitor;
// import ie.francis.fsp.visitor.StringVisitor;

public class Runner {

  public Runner() {
    Environment environment = new Environment();
    environment.loadBuiltins();
  }

  public void compileAndRun(String data, boolean store) {
    Object object = Read.run(data);
    if (object != null) {
      System.out.println(object);
    }
  }
}
