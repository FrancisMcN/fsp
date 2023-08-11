/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import ie.francis.fsp.ast.Visitor;

import static ie.francis.fsp.runtime.type.Type.MACRO;

public class Macro implements DataType {

    private final String name;
    private final String descriptor;

    public Macro(String name, String descriptor) {
        this.name = name;
        this.descriptor = descriptor;
    }

    @Override
    public Type type() {
        return MACRO;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String descriptor() {
        return this.descriptor;
    }

    @Override
    public String toString() {
        return String.format("#%s", name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
