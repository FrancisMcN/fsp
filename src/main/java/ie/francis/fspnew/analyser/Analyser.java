/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.analyser;

import ie.francis.fspnew.node.Node;
import ie.francis.fspnew.visitor.AnalyserVisitor;

public class Analyser {

  private final SymbolTable symbolTable;

  public Analyser(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
  }

  public void analyse(Node tree) {
    AnalyserVisitor visitor = new AnalyserVisitor(symbolTable);
    tree.accept(visitor);
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }
}
