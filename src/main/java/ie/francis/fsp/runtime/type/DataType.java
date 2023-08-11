/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import ie.francis.fsp.ast.Visitor;

public interface DataType {
  Type type();

  String name();

  String descriptor();

  String toString();

  void accept(Visitor visitor);
}
