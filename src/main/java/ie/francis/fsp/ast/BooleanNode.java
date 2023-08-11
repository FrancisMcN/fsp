///*
// * (c) 2023 Francis McNamee
// * */
//
//package ie.francis.fsp.ast;
//
//import static ie.francis.fsp.ast.NodeType.BOOLEAN_NODE;
//
//public class BooleanNode implements Node {
//
//  private final boolean value;
//
//  public BooleanNode(boolean value) {
//    this.value = value;
//  }
//
//  @Override
//  public void accept(Visitor visitor) {
//    visitor.visit(this);
//  }
//
//  @Override
//  public NodeType type() {
//    return BOOLEAN_NODE;
//  }
//
//  @Override
//  public String value() {
//    return String.format("%b", this.value);
//  }
//}
