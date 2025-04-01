/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.loader;

import java.util.HashMap;
import java.util.Map;

public class ParentLispClassLoader extends ClassLoader {

  private final Map<String, LispClassLoader> classloaders;

  public ParentLispClassLoader() {
    this.classloaders = new HashMap<>();
  }

  public Map<String, LispClassLoader> getClassloaders() {
    return classloaders;
  }

  public Class<?> defineClass(String name, byte[] b) {
    classloaders.put(name, new LispClassLoader(this));
    return classloaders.get(name).defineClass(name, b);
  }
}
