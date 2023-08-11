/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.classloader;

public class CustomClassLoader extends ClassLoader {

  private final ParentCustomClassLoader parent;
  private Class<?> clazz;

  public CustomClassLoader(ParentCustomClassLoader parent) {
    this.parent = parent;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (parent.getClassloaders().containsKey(name)) {
      return parent.getClassloaders().get(name).getClazz();
    }
    return super.findClass(name);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    if (parent.getClassloaders().containsKey(name)) {
      return parent.getClassloaders().get(name).getClazz();
    }
    return super.loadClass(name);
  }

  public Class<?> defineClass(String name, byte[] b) {
    clazz = defineClass(name, b, 0, b.length);
    return clazz;
  }
}
