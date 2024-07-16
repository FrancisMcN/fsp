/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import static ie.francis.fsp.runtime.type.Type.FUNCTION;

import ie.francis.fsp.visitor.Visitor;
import java.util.List;

public class Function implements DataType {

  protected String name;
  protected String descriptor;
  protected List<Object> params;

  public Function(String name, String descriptor, List<Object> params) {
    this.name = name;
    this.descriptor = descriptor;
    this.params = params;
  }

  @Override
  public Type type() {
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

  public List<Object> getParams() {
    return this.params;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
