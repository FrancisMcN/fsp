/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.classloader;

import java.util.HashMap;
import java.util.Map;

public class ParentCustomClassLoader extends ClassLoader {

  private final Map<String, CustomClassLoader> classloaders;

  public ParentCustomClassLoader() {
    this.classloaders = new HashMap<>();
  }

  public Map<String, CustomClassLoader> getClassloaders() {
    return classloaders;
  }

  public Class<?> defineClass(String name, byte[] b) {
    classloaders.put(name, new CustomClassLoader(this));
    return classloaders.get(name).defineClass(name, b);
  }
}
