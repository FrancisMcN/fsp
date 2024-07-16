/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.repl;

public class ReplClassLoader extends ClassLoader {
  public ReplClassLoader() {}

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    return super.findClass(name);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    return super.loadClass(name);
  }

  public Class<?> defineClass(String name, byte[] b) {
    return super.defineClass(name, b, 0, b.length);
  }
}
