/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp;

import ie.francis.lisp.compiler.Artifact;
import ie.francis.lisp.function.Compile;
import ie.francis.lisp.function.Read;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {

    Read reader = new Read();
    // Object object = reader.call("(lambda (x y) (+ x y))");
    Object object = reader.call("(quote (1 2 3 4))");

    Compile compiler = new Compile();
    Artifact artifact = (Artifact) compiler.call(object);

    try (FileOutputStream fos = new FileOutputStream(artifact.getName() + ".class")) {
      fos.write(artifact.getData());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
