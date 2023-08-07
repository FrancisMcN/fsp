/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

import static ie.francis.fsp.environment.EntryType.STRING;

public class StringEntry implements Entry {
  private final String name;
  private final String value;

  public StringEntry(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String descriptor() {
    return "Ljava/lang/String";
  }

  @Override
  public EntryType type() {
    return STRING;
  }

  @Override
  public Object value() {
    return value;
  }
}
