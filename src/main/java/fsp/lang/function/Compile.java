/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.function;

import fsp.lang.compiler.Compiler;
import fsp.lang.type.Lambda;

public class Compile extends BaseLambda implements Lambda {

  public static boolean writeToDisk;

  @Override
  public Object call(Object arg) {
    Compiler compiler = new Compiler();
    compiler.setWriteClassesToDisk(writeToDisk);
    return compiler.compile(arg);
  }
}
