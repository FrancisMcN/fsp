/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

import ie.francis.fsp.classloader.ParentCustomClassLoader;
import ie.francis.fsp.runtime.type.Function;
import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.runtime.type.Symbol;

import java.util.HashMap;
import java.util.Map;

public class Environment {

  private final Map<String, DataType> env;
  private final ParentCustomClassLoader classLoader;

  public Environment() {
    env = new HashMap<>();
    classLoader = new ParentCustomClassLoader();
  }

  public DataType get(Symbol symbol) {
    return this.env.get(symbol.name());
  }

  public void put(String name, DataType data) {
    this.env.put(name, data);
  }

  public boolean contains(Symbol symbol) {
    return this.env.containsKey(symbol.name());
  }

  public Class<?> loadClass(String className, byte[] bytes) {
    return classLoader.defineClass(className, bytes);
  }

  public Class<?> loadClass(String className) {
    return classLoader.getClassloaders().get(className).getClazz();
  }

  public void loadBuiltins() {
    env.put(
        "+",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.plus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "-",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.minus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "*",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.multiply",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "/",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.divide",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "<",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.lessThan",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        ">",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.greaterThan",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "=",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.equals",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "concat",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.concat",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "read",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.read",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "car",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.car",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "cdr",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.cdr",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "list",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.list",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    env.put(
        "println",
        new Function(
            "ie/francis/fsp/runtime/builtin/Builtin.println",
            "(Ljava/lang/Object;)Ljava/lang/Object;"));
  }

  @Override
  public String toString() {
    return this.env.toString();
  }
}
