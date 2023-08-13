/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import ie.francis.fsp.runtime.type.Atom;
import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Symbol;

public class AcceptorImpl implements Acceptor {
  @Override
  public void accept(Float floating, Visitor visitor) {
    visitor.visit(floating);
  }

  @Override
  public void accept(Integer integer, Visitor visitor) {
    visitor.visit(integer);
  }

  @Override
  public void accept(String str, Visitor visitor) {
    visitor.visit(str);
  }

  @Override
  public void accept(Symbol symbol, Visitor visitor) {
    visitor.visit(symbol);
  }

  @Override
  public void accept(Cons cons, Visitor visitor) {
    visitor.visit(cons);
  }

  @Override
  public void accept(Atom atom, Visitor visitor) {
    visitor.visit(atom);
  }

  @Override
  public void accept(Boolean bool, Visitor visitor) {
    visitor.visit(bool);
  }

  @Override
  public void accept(Object object, Visitor visitor) {
    if (object instanceof Integer) {
      visitor.visit((Integer) object);
    } else if (object instanceof Float) {
      visitor.visit((Float) object);
    } else if (object instanceof String) {
      visitor.visit((String) object);
    } else if (object instanceof Boolean) {
      visitor.visit((Boolean) object);
    } else if (object instanceof Symbol) {
      visitor.visit((Symbol) object);
    } else if (object instanceof Atom) {
      visitor.visit((Atom) object);
    } else if (object instanceof Cons) {
      visitor.visit((Cons) object);
    }
  }
}
