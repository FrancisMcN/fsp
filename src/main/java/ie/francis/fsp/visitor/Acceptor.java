/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import ie.francis.fsp.runtime.type.Atom;
import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Symbol;

public interface Acceptor {
  void accept(Float floating, Visitor visitor);

  void accept(Integer integer, Visitor visitor);

  void accept(String str, Visitor visitor);

  void accept(Symbol symbol, Visitor visitor);

  void accept(Cons cons, Visitor visitor);

  void accept(Atom atom, Visitor visitor);

  void accept(Boolean bool, Visitor visitor);

  void accept(Object object, Visitor visitor);
}
