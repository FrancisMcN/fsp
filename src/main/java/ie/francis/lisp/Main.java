/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp;

import ie.francis.lisp.compiler.Artifact;
import ie.francis.lisp.function.Compile;
import ie.francis.lisp.function.Read;
import ie.francis.lisp.loader.LispClassLoader;
import ie.francis.lisp.type.Lambda;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Main {
  public static void main(String[] args)
      throws ClassNotFoundException,
          NoSuchMethodException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {

    Read reader = new Read();
    Object object = reader.call("((lambda (x y) x) \"hello world\" 2)");
    //     Object object = reader.call("(lambda (x y) 123)");
    //    Object object = reader.call("(quote (1 2 3 4))");

    Compile compiler = new Compile();
    List<Artifact> artifacts = (List<Artifact>) compiler.call(object);

    LispClassLoader lispClassLoader = new LispClassLoader();
    for (Artifact artifact : artifacts) {
      lispClassLoader.defineClass(artifact.getName(), artifact.getData());
      try (FileOutputStream fos = new FileOutputStream(artifact.getName() + ".class")) {
        fos.write(artifact.getData());
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    Artifact artifactToRun = artifacts.get(artifacts.size() - 1);
    Class<?> c = lispClassLoader.loadClass(artifactToRun.getName());
    Constructor<?> ctor = c.getConstructor();
    Object lambda = ctor.newInstance();
    Lambda l = ((Lambda) lambda);

    System.out.println(String.format("= %s", l.call()));
  }
}
