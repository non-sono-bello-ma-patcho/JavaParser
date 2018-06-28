package visitors;

import Parser.ast.Exp;
import Parser.ast.ExpSeq;
import Parser.ast.Ident;
import Parser.ast.Stmt;
import Parser.ast.StmtSeq;

public interface Visitor<T> {
	T visitAdd(Exp left, Exp right);

	T visitEquals(Exp left, Exp right);

	T visitAnd(Exp left, Exp right);

	T visitOr(Exp left, Exp right);

	T visitAssignStmt(Ident ident, Exp exp);

	T visitForEachStmt(Ident ident, Exp exp, StmtSeq block);

	T visitDoWhileStmt(Exp exp, StmtSeq block);

	T visitIntLiteral(int value);

	T visitBoolLiteral(boolean value);

	T visitListLiteral(ExpSeq exps);

	T visitMoreExp(Exp first, ExpSeq rest);

	T visitMoreStmt(Stmt first, StmtSeq rest);

	T visitMul(Exp left, Exp right);

	T visitPrefix(Exp left, Exp right);

	T visitPrintStmt(Exp exp);

	T visitProg(StmtSeq stmtSeq);

	T visitSign(Exp exp);

	T visitNot(Exp exp);

	T visitGet(Exp exp);

	T visitIdent(String name);

	T visitSingleExp(Exp exp);

	T visitSingleStmt(Stmt stmt);

	T visitVarStmt(Ident ident, Exp exp);

	T visitIfStmt(Exp exp, StmtSeq stmt);

	T visitIfElseStmt(Exp exp, StmtSeq stmtIf, StmtSeq stmtElse);
}
