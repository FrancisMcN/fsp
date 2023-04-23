/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.ast;

public interface Node {
  void accept(Visitor visitor);
}
