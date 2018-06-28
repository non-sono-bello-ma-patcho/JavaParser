package Parser.ast;

import visitors.Visitor;

public class Empty extends UnaryOp {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return null;
    }
}
