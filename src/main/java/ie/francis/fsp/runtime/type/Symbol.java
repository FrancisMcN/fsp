package ie.francis.fsp.runtime.type;

import ie.francis.fsp.ast.Visitor;

import static ie.francis.fsp.runtime.type.Type.SYMBOL;

public class Symbol implements DataType {

    private final String value;

    public Symbol(String value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return SYMBOL;
    }

    @Override
    public String name() {
        return this.value;
    }

    @Override
    public String descriptor() {
        return null;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
