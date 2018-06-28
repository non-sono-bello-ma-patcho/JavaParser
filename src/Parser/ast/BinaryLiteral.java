package Parser.ast;

import visitors.Visitor;

public class BinaryLiteral extends PrimLiteral<Integer> {

    public BinaryLiteral(int n) {
        super(n);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitIntLiteral(value);
    }

}
