package Parser.ast;

import visitors.Visitor;

public class Equals extends BinaryOp {
    public Equals(Exp left, Exp right) {
        super(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitEquals(left, right);
    }
}
