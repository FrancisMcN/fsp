/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

import static ie.francis.fsp.environment.EntryType.FUNCTION;

public class FunctionEntry implements Entry {

  private final String name;
  private final String descriptor;

  public FunctionEntry(String name, String descriptor) {
    this.name = name;
    this.descriptor = descriptor;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String descriptor() {
    return descriptor;
  }

  @Override
  public EntryType type() {
    return FUNCTION;
  }

  @Override
  public Object value() {
    return null;
  }

  @Override
  public String toString() {
    return "FunctionEntry{" + "name='" + name + '\'' + ", descriptor='" + descriptor + '\'' + '}';
  }
}
