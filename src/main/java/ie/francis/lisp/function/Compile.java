/*
 * (c) 2025 Francis McNamee
 * */
 
package ie.francis.lisp.function;

import ie.francis.lisp.compiler.Compiler;
import ie.francis.lisp.type.Lambda;

public class Compile extends BaseLambda implements Lambda {

  public static boolean writeToDisk;

  @Override
  public Object call(Object arg) {
    Compiler compiler = new Compiler();
    compiler.setWriteClassesToDisk(writeToDisk);
    return compiler.compile(arg);
  }
}
