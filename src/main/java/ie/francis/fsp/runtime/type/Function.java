/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import static ie.francis.fsp.runtime.type.DataType.FUNCTION;

public class Function implements Type {

  private final String name;
  private final String descriptor;

  public Function(String name, String descriptor) {
    this.name = name;
    this.descriptor = descriptor;
  }

  @Override
  public DataType type() {
    return FUNCTION;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String descriptor() {
    return this.descriptor;
  }

  @Override
  public String toString() {
    return String.format("#%s", name);
  }
}
