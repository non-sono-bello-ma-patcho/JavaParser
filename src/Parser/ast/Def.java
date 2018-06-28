package Parser.ast;

import visitors.Visitor;

import java.util.function.UnaryOperator;

public class Def extends UnaryOp {
    public Def(Exp exp){super(exp);}

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDef(exp);
    }
}
