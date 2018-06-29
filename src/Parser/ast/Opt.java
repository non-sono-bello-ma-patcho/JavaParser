package Parser.ast;

import visitors.Visitor;

public class Opt extends UnaryOp {

    public Opt(Exp exp){
        super(exp);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitOpt(exp);
    }
}
