/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

import static ie.francis.fsp.ast.NodeType.NUMBER_NODE;

public class NumberNode implements Node {

  private final boolean isInteger;
  private final int intValue;
  private final float floatValue;

  public NumberNode(int intValue) {
    this.isInteger = true;
    this.intValue = intValue;
    this.floatValue = 0;
  }

  public NumberNode(float floatValue) {
    this.isInteger = false;
    this.intValue = 0;
    this.floatValue = floatValue;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public NodeType type() {
    return NUMBER_NODE;
  }

  @Override
  public String value() {
    if (this.isInteger) {
      return String.format("%d", this.intValue);
    }
    return String.format("%f", this.floatValue);
  }

  @Override
  public String toString() {
    if (this.isInteger) {
      return String.format("IntNumber(%d)", this.intValue);
    }
    return String.format("FloatNumber(%f)", this.floatValue);
  }

  public int getIntValue() {
    return intValue;
  }

  public float getFloatValue() {
    return floatValue;
  }

  public boolean isInteger() {
    return isInteger;
  }
}
