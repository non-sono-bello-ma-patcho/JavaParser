package Parser.ast;

import static java.util.Objects.requireNonNull;
import visitors.Visitor;

public class IfStmt {
    private final Exp exp;
    private final StmtSeq block;
    private final StmtSeq elseStmt;

    public IfStmt(Exp exp, StmtSeq block) {
        this.exp = requireNonNull(exp); //TODO boolexp
        this.block = requireNonNull(block);
        this.elseStmt = null;
    }
    
    public IfStmt(Exp exp, StmtSeq block, StmtSeq elseStmt){
        this.exp = requireNonNull(exp); //TODO boolexp
        this.block = requireNonNull(block);
        this.elseStmt = requireNonNull(elseStmt);
    }

    @Override
    public String toString() {
        if(elseStmt == null)
            return getClass().getSimpleName() + "(" + exp + "," + block + ")";
        return getClass().getSimpleName() + "(" + exp + "," + block + "," + elseStmt + ")";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        if(elseStmt == null)
            return visitor.visitIfStmt(exp, block);
        return visitor.visitIfElseStmt(exp,block,elseStmt);

    }
    
    
}
