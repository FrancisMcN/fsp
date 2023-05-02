/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

public class SxprNode implements Node {

  private Node car;
  private Node cdr;

  public SxprNode() {}

  public void setCar(Node car) {
    this.car = car;
  }

  public Node getCar() {
    return car;
  }

  public void setCdr(Node cdr) {
    this.cdr = cdr;
  }

  public Node getCdr() {
    return cdr;
  }

  @Override
  public String toString() {
    return String.format("SxprNode(%s, %s)", this.car, this.cdr);
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
