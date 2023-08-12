package ie.francis.fsp.runtime.type;

import ie.francis.fsp.visitor.Visitor;

import static ie.francis.fsp.runtime.type.Type.BOOL;

public class Bool implements DataType {

    private final boolean value;

    public Bool(boolean value) {
        this.value = value;
    }
    @Override
    public Type type() {
        return BOOL;
    }

    @Override
    public String name() {
        return "bool";
    }

    @Override
    public String descriptor() {
        return "";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("%b", this.value);
    }
}
