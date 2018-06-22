package Parser.ast;

import static java.util.Objects.requireNonNull;

import visitors.Visitor;

public class DoWhileStmt implements Stmt {
    private final Exp exp; // TODO: add boolExp;
    private final StmtSeq block;

    public DoWhileStmt(Exp exp, StmtSeq block) {
        this.exp = requireNonNull(exp);
        this.block = requireNonNull(block);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + block +  "," + exp + ")";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDoWhileStmt(exp, block);
    }

}
