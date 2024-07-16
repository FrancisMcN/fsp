/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

import ie.francis.fspnew.visitor.Visitor;

public interface Node {
  NodeType type();

  void accept(Visitor visitor);

  Object eval();

  Node quote();
}
