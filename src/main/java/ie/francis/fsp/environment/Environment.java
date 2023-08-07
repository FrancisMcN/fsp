/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

import ie.francis.fsp.classloader.CustomClassLoader;
import java.util.HashMap;
import java.util.Map;

public class Environment {

  private final Map<String, Entry> env;
  private final CustomClassLoader classLoader;

  public Environment() {
    env = new HashMap<>();
    classLoader = new CustomClassLoader();
  }

  public Entry get(String name) {
    return this.env.get(name);
  }

  public void put(String name, Entry entry) {
    this.env.put(name, entry);
  }

  public boolean contains(String name) {
    return this.env.containsKey(name);
  }

  public Class loadClass(String className, byte[] bytes) {
    return classLoader.defineClass(className, bytes);
  }

  public void loadBuiltins() {
    env.put(
        "+",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.plus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "-",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.minus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "*",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.multiply",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "/",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.divide",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "<",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.lessThan",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        ">",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.greaterThan",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "=",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.equals",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "concat",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.concat",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "read",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.read",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "car",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.car",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "cdr",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.cdr",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "println",
        new FunctionEntry(
            "ie/francis/fsp/runtime/builtin/Builtin.println",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
  }

  @Override
  public String toString() {
    return this.env.toString();
  }
}
