/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

import ie.francis.fsp.classloader.ParentCustomClassLoader;
import ie.francis.fsp.runtime.builtin.*;
import ie.francis.fsp.runtime.type.Symbol;
import java.util.HashMap;
import java.util.Map;

public class Environment {

  private static Environment singleton;
  private final Map<String, Object> env;
  private final ParentCustomClassLoader classLoader;

  private final Map<String, byte[]> classes;

  public Environment() {
    env = new HashMap<>();
    classes = new HashMap<>();
    classLoader = new ParentCustomClassLoader();
    if (Environment.singleton == null) {
      Environment.singleton = this;
    }
  }

  public static Environment singleton() {
    return Environment.singleton;
  }

  public Object get(Symbol symbol) {
    return this.env.get(symbol.name());
  }

  public void put(String name, Object data) {
    this.env.put(name, data);
  }

  public boolean contains(Symbol symbol) {
    return this.env.containsKey(symbol.name());
  }

  public Class<?> loadClass(String className, byte[] bytes) {
    return classLoader.defineClass(className, bytes);
  }

  public Class<?> loadClass(String className, byte[] bytes, boolean store) {
    if (store) {
      classes.put(className, bytes);
    }
    return loadClass(className, bytes);
  }

  public Class<?> loadClass(String className) {
    return classLoader.getClassloaders().get(className).getClazz();
  }

  public byte[] getClassBytes(String className) {
    return classes.get(className);
  }

  public void loadBuiltins() {

    env.put("+", new Plus());
    env.put("-", new Minus());
    env.put("*", new Multiply());
    env.put("/", new Divide());
    env.put("<", new LessThan());
    env.put(">", new GreaterThan());
    env.put("=", new Equals());
    env.put("concat", new Concat());
    env.put("car", new Car());
    env.put("cdr", new Cdr());
    env.put("list", new List());
    env.put("println", new Println());
    env.put("read", new Read());
    env.put("macroexpand-1", new Macroexpand1());
  }

  @Override
  public String toString() {
    return this.env.toString();
  }
}
