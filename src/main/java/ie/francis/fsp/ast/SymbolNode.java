///*
// * (c) 2023 Francis McNamee
// * */
//
//package ie.francis.fsp.ast;
//
//import static ie.francis.fsp.ast.NodeType.SYMBOL_NODE;
//
//public class SymbolNode implements Node {
//
//  private final String symbol;
//
//  public SymbolNode(String symbol) {
//    this.symbol = symbol;
//  }
//
//  @Override
//  public void accept(Visitor visitor) {
//    visitor.visit(this);
//  }
//
//  @Override
//  public NodeType type() {
//    return SYMBOL_NODE;
//  }
//
//  @Override
//  public String value() {
//    return this.symbol;
//  }
//
//  @Override
//  public String toString() {
//    return this.symbol;
//  }
//}
