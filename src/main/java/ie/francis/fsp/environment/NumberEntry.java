/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

import static ie.francis.fsp.environment.EntryType.NUMBER;

public class NumberEntry implements Entry {

  private final String name;
  private final Integer value;

  public NumberEntry(String name, Integer value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String descriptor() {
    return "I";
  }

  @Override
  public EntryType type() {
    return NUMBER;
  }

  @Override
  public Object value() {
    return value;
  }
}
