package Parser.ast;

import visitors.Visitor;

public class BoolLiteral extends PrimLiteral<Boolean> {

    public BoolLiteral(boolean n) {
        super(n);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBoolLiteral(value);
    }

}
