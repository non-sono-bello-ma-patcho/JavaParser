package visitors.evaluation;

import environments.EnvironmentException;
import environments.GenEnvironment;
import Parser.ast.Exp;
import Parser.ast.ExpSeq;
import Parser.ast.Ident;
import Parser.ast.SimpleIdent;
import Parser.ast.Stmt;
import Parser.ast.StmtSeq;
import visitors.Visitor;
import visitors.typechecking.Type;

public class Eval implements Visitor<Value> {

	private final GenEnvironment<Value> env = new GenEnvironment<>();

	// dynamic semantics for programs; no value returned by the visitor

	@Override
	public Value visitProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
		} catch (EnvironmentException e) { // undefined variable
			throw new EvaluatorException(e);
		}
		return null;
	}

	// dynamic semantics for statements; no value returned by the visitor

	@Override
	public Value visitAssignStmt(Ident ident, Exp exp) {
		env.update(ident, exp.accept(this));
		return null;
	}

	@Override
	public Value visitForEachStmt(Ident ident, Exp exp, StmtSeq block) {
		ListValue list = exp.accept(this).asList();
		for (Value val : list) {
			env.enterLevel();
			env.dec(ident, val);
			block.accept(this);
			env.exitLevel();
		}
		return null;
	}

	@Override
	public Value visitDoWhileStmt(Exp exp, StmtSeq block) {
		do{
		    block.accept(this);
        }while(exp.accept(this).asBool());
		return null;
	}

	@Override
	public Value visitPrintStmt(Exp exp) {
		System.out.println(exp.accept(this));
		return null;
	}

	@Override
	public Value visitVarStmt(Ident ident, Exp exp) {
		env.dec(ident, exp.accept(this));
		return null;
	}

	@Override
	public Value visitIfStmt(Exp exp, StmtSeq stmt) {
		if(exp.accept(this).asBool())
		    stmt.accept(this);
		return null;
	}

	@Override
	public Value visitIfElseStmt(Exp exp, StmtSeq stmtIf, StmtSeq stmtElse) {
		if(exp.accept(this).asBool())
		    stmtIf.accept(this);
		else stmtElse.accept(this);

		return null;
	}

	@Override
	public Value visitDef(Exp exp) {
		return new BoolValue(!exp.accept(this).asOpt().isEmpty());
	}

	@Override
	public Value visitEmpty(Exp exp) {
		OptlValue ov = new OptlValue(exp.accept(this).asOpt());
		ov.
		return ;
	}

	// dynamic semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Value visitSingleStmt(Stmt stmt) {
		stmt.accept(this);
		return null;
	}

	@Override
	public Value visitMoreStmt(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	// dynamic semantics of expressions; a value is returned by the visitor

	@Override
	public Value visitAdd(Exp left, Exp right) {
		return new IntValue(left.accept(this).asInt() + right.accept(this).asInt());
	}

	@Override
	public Value visitEquals(Exp left, Exp right) {
		return new BoolValue(left.accept(this).equals(right.accept(this)));
	}

	@Override
	public Value visitAnd(Exp left, Exp right) {
		return new BoolValue(left.accept(this).equals(true) && right.accept(this).equals(true));
	}

	@Override
	public Value visitOr(Exp left, Exp right) {
		return new BoolValue(left.accept(this).equals(true) || right.accept(this).equals(true));
	}

	@Override
	public Value visitIntLiteral(int value) {
		return new IntValue(value);
	}

	@Override
	public Value visitBoolLiteral(boolean value) {
		return new BoolValue(value);
	}

	@Override
	public Value visitListLiteral(ExpSeq exps) {
		return exps.accept(this);
	}

	@Override
	public Value visitMul(Exp left, Exp right) {
		return new IntValue(left.accept(this).asInt() * right.accept(this).asInt());
	}

	@Override
	public Value visitPrefix(Exp left, Exp right) {
		Value el = left.accept(this);
		return right.accept(this).asList().prefix(el);
	}

	@Override
	public Value visitSign(Exp exp) {
		return new IntValue(-exp.accept(this).asInt());
	}

	@Override
	public Value visitNot(Exp exp) {
	    return new BoolValue(!exp.accept(this).asBool());
	}

	@Override
	public Value visitGet(Exp exp) {
		Value val = exp.accept(this);
		return null;
	}

	@Override
	public Value visitIdent(String name) {
		return env.lookup(new SimpleIdent(name));
	}

	@Override
	public Value visitOpt(Exp exp) {
		return null;
	}

	// dynamic semantics of sequences of expressions
	// a list of values is returned by the visitor

	@Override
	public Value visitSingleExp(Exp exp) {
		return new ListValue(exp.accept(this), new ListValue());
	}

	@Override
	public Value visitMoreExp(Exp first, ExpSeq rest) {
		return new ListValue(first.accept(this), rest.accept(this).asList());
	}

}

