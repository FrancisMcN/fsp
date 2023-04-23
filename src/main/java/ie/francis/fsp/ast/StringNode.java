/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

public class StringNode implements Node {

  private final String str;

  public StringNode(String str) {
    this.str = str;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("String(%s)", this.str);
  }
}
