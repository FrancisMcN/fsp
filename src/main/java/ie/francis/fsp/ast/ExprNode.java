/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

public class ExprNode implements Node {

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
