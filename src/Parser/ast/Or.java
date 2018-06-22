package Parser.ast;

import visitors.Visitor;

public class Or extends BinaryOp {
    public Or(Exp left, Exp right) {
        super(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitOr(left, right);
    }
}
