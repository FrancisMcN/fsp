package ie.francis.fsp.runtime.type;

import ie.francis.fsp.ast.Visitor;

import static ie.francis.fsp.runtime.type.Type.STRING;
import static ie.francis.fsp.runtime.type.Type.SYMBOL;

public class FspString implements DataType {

    private final String value;

    public FspString(String value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return STRING;
    }

    @Override
    public String name() {
        return this.value;
    }

    @Override
    public String descriptor() {
        return "";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
