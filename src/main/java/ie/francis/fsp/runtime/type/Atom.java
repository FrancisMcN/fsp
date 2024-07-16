/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import static ie.francis.fsp.runtime.type.Type.*;

import ie.francis.fsp.visitor.Visitor;

public class Atom implements DataType {
  private String str;

  private Symbol symbol;
  private Integer integer;
  private Float floating;
  private Boolean bool;

  private final Type type;

  public Atom(String str) {
    this.str = str;
    this.type = STRING;
  }

  public String getString() {
    return str;
  }

  public Atom(Symbol symbol) {
    this.symbol = symbol;
    this.type = SYMBOL;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public Atom(Integer integer) {
    this.integer = integer;
    this.type = NUMBER;
  }

  public Integer getInteger() {
    return integer;
  }

  public Atom(Float floating) {
    this.floating = floating;
    this.type = NUMBER;
  }

  public Float getFloat() {
    return floating;
  }

  public Atom(Boolean bool) {
    this.bool = bool;
    this.type = BOOL;
  }

  public Boolean getBool() {
    return bool;
  }

  @Override
  public Type type() {
    return this.type;
  }

  @Override
  public String name() {
    return "atom";
  }

  @Override
  public String descriptor() {
    switch (this.type) {
      case STRING:
        return "Ljava/lang/String;";
      case SYMBOL:
        return "ie/francis/fsp/runtime/type/Symbol";
      case NUMBER:
        if (floating != null) {
          return "F";
        }
        return "I";
      case BOOL:
        return "Z";
    }
    return null;
  }

  public Object unwrap() {
    switch (this.type) {
      case STRING:
        return this.getString();
      case SYMBOL:
        return this.getSymbol();
      case NUMBER:
        if (floating != null) {
          return this.getFloat();
        }
        return this.getInteger();
      case BOOL:
        return this.getBool();
    }
    return null;
  }

  @Override
  public void accept(Visitor visitor) {}
}
