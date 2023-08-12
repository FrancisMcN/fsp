package ie.francis.fsp.runtime.type;

import ie.francis.fsp.visitor.Visitor;

import static ie.francis.fsp.runtime.type.Type.NUMBER;

public class Number implements DataType {

    private Integer ivalue;
    private Float fvalue;

    public Number(int value) {
        this.ivalue = value;
    }

    public Number(float value) {
        this.fvalue = value;
    }

    @Override
    public Type type() {
        return NUMBER;
    }

    @Override
    public String name() {
        return "number";
    }

    @Override
    public String descriptor() {
        return "";
    }

    public int getIValue() {
        return this.ivalue;
    }

    public float getFValue() {
        return this.fvalue;
    }

    public boolean isFloat() {
        return fvalue != null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        if (ivalue != null) {
            return String.format("%d", ivalue);
        }
        return String.format("%f", fvalue);
    }
}
