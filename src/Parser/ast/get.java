package Parser.ast;

import visitors.Visitor;

public class get extends UnaryOp {

    public get(Exp exp) {
        super(exp);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitGet(exp);
    }
}

