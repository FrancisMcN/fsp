/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import ie.francis.fsp.runtime.type.*;

public interface Visitor {

  void visit(Integer integer);

  void visit(Float floating);

  void visit(Cons cons);

  void visit(Boolean bool);

  void visit(Symbol symbol);

  void visit(String str);

  void visit(Atom atom);

  void visit(Function function);

  void visit(Macro macro);
}
