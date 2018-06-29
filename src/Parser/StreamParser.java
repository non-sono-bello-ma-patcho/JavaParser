package Parser;

import static Parser.TokenType.*;
import Parser.ast.*;


/*
Prog ::= StmtSeq 'EOF'
 StmtSeq ::= Stmt (';' StmtSeq)?
 Stmt ::= 'var'? ID '=' Exp | 'print' Exp |  'for' ID ':' Exp '{' StmtSeq '}' | if (Exp) {StmtSeq} (else {StmtSeq})? | do {StmtSeq} while (Exp)
 ExpSeq ::= Exp (',' ExpSeq)?
 Exp ::= Add ('::' Exp)? | Exp && Exp | Exp == Exp | ! Exp | opt Exp | empty Exp | def Exp | get Exp |true |false|binaryExpr
 Add ::= Mul ('+' Mul)*
 Mul::= Atom ('*' Atom)*
 Atom ::= '-' Atom | '[' ExpSeq ']' | NUM | ID | '(' Exp ')'

*/

public class StreamParser implements Parser {
	private final Tokenizer tokenizer;

	private void tryNext() throws ParserException {
		try {
			tokenizer.next();
		} catch (TokenizerException e) {
			throw new ParserException(e);
		}
	}

	private void match(TokenType expected) throws ParserException {
		final TokenType found = tokenizer.tokenType();
		if (found != expected)
			throw new ParserException(
					"Expecting " + expected + ", found " + found + "('" + tokenizer.tokenString() + "')");
	}

	private void consume(TokenType expected) throws ParserException {
		match(expected);
		tryNext();
	}

	private void unexpectedTokenError() throws ParserException {
		throw new ParserException("Unexpected token " + tokenizer.tokenType() + "('" + tokenizer.tokenString() + "')");
	}

	public StreamParser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	@Override
	public Prog parseProg() throws ParserException {
		tryNext(); // one look-ahead symbol
		Prog prog = new ProgClass(parseStmtSeq());
		match(EOF);
		return prog;
	}

	private StmtSeq parseStmtSeq() throws ParserException {
		Stmt stmt = parseStmt();
		if (tokenizer.tokenType() == STMT_SEP) {
			tryNext();
			return new MoreStmt(stmt, parseStmtSeq());
		}
		return new SingleStmt(stmt);
	}

	private ExpSeq parseExpSeq() throws ParserException {
		Exp exp = parseExp();
		if (tokenizer.tokenType() == EXP_SEP) {
			tryNext();
			return new MoreExp(exp, parseExpSeq());
		}
		return new SingleExp(exp);
	}

	private Stmt parseStmt() throws ParserException {
		switch (tokenizer.tokenType()) {
		default:
			unexpectedTokenError();
		case PRINT:
			return parsePrintStmt();
		case VAR:
			return parseVarStmt();
		case IDENT:
			return parseAssignStmt();
		case FOR:
			return parseForEachStmt();
         case IF:
             return parseIfStmt();
         case DO:
             return parseDoWhileStmt();
		}
	}

	private PrintStmt parsePrintStmt() throws ParserException {
		consume(PRINT); // or tryNext();
		return new PrintStmt(parseExp());
	}

	private VarStmt parseVarStmt() throws ParserException {
		consume(VAR); // or tryNext();
		Ident ident = parseIdent();
		consume(ASSIGN);
		return new VarStmt(ident, parseExp());
	}

	private AssignStmt parseAssignStmt() throws ParserException {
		Ident ident = parseIdent();
		consume(ASSIGN);
		return new AssignStmt(ident, parseExp());
	}

	private DoWhileStmt parseDoWhileStmt() throws  ParserException{
		consume(DO);
		consume(OPEN_BLOCK);
		StmtSeq sq = parseStmtSeq();
		consume(CLOSE_BLOCK);
		consume(OPEN_PAR);
		Exp e = parseExp(); // TODO: add boolExp
		consume(CLOSE_PAR);
		return new DoWhileStmt(e, sq);
	}

	private ForEachStmt parseForEachStmt() throws ParserException {
		consume(FOR); // or tryNext();
		Ident ident = parseIdent();
		consume(IN);
		Exp exp = parseExp();
		consume(OPEN_BLOCK);
		StmtSeq stmts = parseStmtSeq();
		consume(CLOSE_BLOCK);
		return new ForEachStmt(ident, exp, stmts);
	}
    /*ci vorrebbe la expr booleana*/
	private IfStmt parseIfStmt() throws ParserException{
	    consume(IF);
	    consume(OPEN_PAR);
	    Exp exp = parseExp(); //TODO: boolexp
	    consume(CLOSE_PAR);
        consume(OPEN_BLOCK);
	    StmtSeq Firststmts = parseStmtSeq();
        consume(CLOSE_BLOCK);
        if (tokenizer.tokenType() == ELSE){
            tryNext();
            consume(OPEN_BLOCK);
            StmtSeq Secondstmts = parseStmtSeq();
            consume(CLOSE_BLOCK);
            return new IfStmt(exp,Firststmts,Secondstmts); //TODO boolexp
        }
        return new IfStmt(exp,Firststmts); //TODO: boolexp
    }

	private Exp parseExp() throws ParserException { //TODO: capire come distinguere quando usare aritmetici e quando logici...
		Exp exp = null;
		if(tokenizer.tokenType() == NUM) {
		    tryNext();
            System.err.println("token dopo trynext = "+ tokenizer.tokenString());

            if(tokenizer.tokenType() == EQUAL) {
                System.err.println("sono qui prima della parseequal()");
                parseEqual();
                System.err.println("sono qui");
            }
		    else {
                exp = parseAdd();
                if (tokenizer.tokenType() == PREFIX) {
                    tryNext();
                    exp = new Prefix(exp, parseExp());
                }
            }
		}
		else{
			exp = parseOr();
		}
		return exp;
	}

	private Exp parseAdd() throws ParserException {
		Exp exp = parseMul();
		while (tokenizer.tokenType() == PLUS) {
			tryNext();
			exp = new Add(exp, parseMul());
		}
		return exp;
	}

	private Exp parseOr() throws ParserException {
		Exp exp = parseMul();
		while (tokenizer.tokenType() == OR) {
			tryNext();
			exp = new Or(exp, parseAnd());
		}
		return exp;
	}

	private Exp parseMul() throws ParserException {
		Exp exp = parseAtom();
		while (tokenizer.tokenType() == TIMES) {
			tryNext();
			exp = new Mul(exp, parseAtom());
		}
		return exp;
	}

	private Exp parseAnd() throws ParserException {
		Exp exp = parseAtom();
		while (tokenizer.tokenType() == AND) {
			tryNext();
			exp = new And(exp, parseAtom());
		}
		return exp;
	}

	private Exp parseAtom() throws ParserException {
		switch (tokenizer.tokenType()) {
		default:
			unexpectedTokenError();
		case BOOLEAN:
			return parseBool();
		case NUM:
			return parseNum();
		case IDENT:
			return parseIdent();
		case MINUS:
			return parseMinus();
		case OPEN_LIST:
			return parseList();
		case OPEN_PAR:
			return parseRoundPar();
        case GET:
            return parseGet();
		case OPT:
			return parseOpt();
		case DEF:
			return parseDef();
        case EMPTY:
            return parseEmpty();
        case NOT:
            return parseNot();
		}
	}


	private Not parseNot() throws ParserException{
	    consume(NOT);
	    return new Not(parseExp());
    }

    private Empty parseEmpty() throws ParserException{
        consume(EMPTY);
        return new Empty(parseExp());
    }

	private Equals parseEqual() throws ParserException {
		Exp sx = parseExp();
		consume(EQUAL);
		return new Equals(sx, parseExp());
	}

	private IntLiteral parseNum() throws ParserException {
		int val = tokenizer.intValue();
		consume(NUM); // or tryNext();
		return new IntLiteral(val);
	}

    private Opt parseOpt() throws ParserException {
        consume(OPT);
        return new Opt(parseExp());
    }

    private Def parseDef() throws ParserException {
	    consume(DEF);
	    return new Def(parseExp());
    }

	private BoolLiteral parseBool() throws ParserException {
		boolean val = tokenizer.boolValue();
		consume(BOOLEAN);
		return new BoolLiteral(val);
	}

	private Ident parseIdent() throws ParserException {
		String name = tokenizer.tokenString();
		consume(IDENT); // or tryNext();
		return new SimpleIdent(name);
	}

	private Sign parseMinus() throws ParserException {
		consume(MINUS); // or tryNext();
		return new Sign(parseAtom());
	}

	private ListLiteral parseList() throws ParserException {
		consume(OPEN_LIST); // or tryNext();
		ExpSeq exps = parseExpSeq();
		consume(CLOSE_LIST);
		return new ListLiteral(exps);
	}

	private get parseGet() throws ParserException{
	    consume(GET);
	    return new get(parseExp());
    }
	private Exp parseRoundPar() throws ParserException {
		consume(OPEN_PAR); // or tryNext();
		Exp exp = parseExp();
		consume(CLOSE_PAR);
		return exp;
	}

}
