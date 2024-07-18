/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.compiler.Artifact;
import ie.francis.lisp.loader.LispClassLoader;
import ie.francis.lisp.type.Lambda;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Eval extends BaseLambda implements Lambda {

  static LispClassLoader lispClassLoader = new LispClassLoader();

  @Override
  public Object call(Object arg) {
    Compile compiler = new Compile();
    List<Artifact> artifacts = (List<Artifact>) compiler.call(arg);

    for (Artifact artifact : artifacts) {
      lispClassLoader.defineClass(artifact.getName(), artifact.getData());
//       System.out.println("defined class: " + artifact.getName());
//      try (FileOutputStream fos = new FileOutputStream(artifact.getName() + ".class")) {
//        fos.write(artifact.getData());
//      } catch (IOException ex) {
//        throw new RuntimeException(ex);
//      }
    }

    Artifact artifactToRun = artifacts.get(artifacts.size() - 1);
    try {
      Class<?> c = lispClassLoader.loadClass(artifactToRun.getName());
      Constructor<?> ctor = c.getConstructor();
      Object lambda = ctor.newInstance();
      Lambda l = ((Lambda) lambda);
      return l.call();
    } catch (ClassNotFoundException
        | InvocationTargetException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
