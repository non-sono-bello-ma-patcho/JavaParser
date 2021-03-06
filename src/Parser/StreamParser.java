package Parser;

import static Parser.TokenType.*;
import Parser.ast.*;


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
		Exp exp = parseAnd();
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
		return new PrintStmt(parseAnd());
	}

	private VarStmt parseVarStmt() throws ParserException {
		consume(VAR); // or tryNext();
		Ident ident = parseIdent();
		consume(ASSIGN);
		return new VarStmt(ident, parseAnd());
	}

	private AssignStmt parseAssignStmt() throws ParserException {
		Ident ident = parseIdent();
		consume(ASSIGN);
		return new AssignStmt(ident, parseAnd());
	}

	private DoWhileStmt parseDoWhileStmt() throws  ParserException{
		consume(DO);
		consume(OPEN_BLOCK);
		StmtSeq sq = parseStmtSeq();
		consume(CLOSE_BLOCK);
		consume(WHILE);
		return new DoWhileStmt(parseRoundPar(), sq);
	}

	private ForEachStmt parseForEachStmt() throws ParserException {
		consume(FOR); // or tryNext();
		Ident ident = parseIdent();
		consume(IN);
		Exp exp = parseAnd();
		consume(OPEN_BLOCK);
		StmtSeq stmts = parseStmtSeq();
		consume(CLOSE_BLOCK);
		return new ForEachStmt(ident, exp, stmts);
	}
    /*ci vorrebbe la expr booleana*/
	private IfStmt parseIfStmt() throws ParserException{
	    consume(IF);
	    consume(OPEN_PAR);
	    Exp exp = parseAnd();
	    consume(CLOSE_PAR);
        consume(OPEN_BLOCK);
	    StmtSeq Firststmts = parseStmtSeq();
        consume(CLOSE_BLOCK);
        if (tokenizer.tokenType() == ELSE){
            tryNext();
            consume(OPEN_BLOCK);
            StmtSeq Secondstmts = parseStmtSeq();
            consume(CLOSE_BLOCK);
            return new IfStmt(exp,Firststmts,Secondstmts);
        }
        return new IfStmt(exp,Firststmts);
    }

    private Exp parseExp() throws ParserException{
	    Exp e = parseAdd();
	    while(tokenizer.tokenType() == PREFIX){
	        tryNext();
	        e = new Prefix(e, parseAdd());
        }
	    return e;
    }

	private Exp parseAnd() throws ParserException {
        Exp exp = parseEqual();
        while (tokenizer.tokenType() == AND) {
            consume(AND);
            exp = new And(exp, parseAnd());
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

	private Exp parseMul() throws ParserException {
		Exp exp = parseAtom();
		while (tokenizer.tokenType() == TIMES) {
			tryNext();
			exp = new Mul(exp, parseAtom());
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
	    return new Not(parseAtom());
    }

    private Empty parseEmpty() throws ParserException{
        consume(EMPTY);
        return new Empty(parseAtom());
    }

	private Exp parseEqual() throws ParserException {
		Exp sx = parseExp();
		while(tokenizer.tokenType()==EQUAL) {
            consume(EQUAL);
            sx = new Equals(sx, parseExp());
        }
        return sx;
	}

	private IntLiteral parseNum() throws ParserException {
		int val = tokenizer.intValue();
		consume(NUM); // or tryNext();
		return new IntLiteral(val);
	}

    private Opt parseOpt() throws ParserException {
        consume(OPT);
        return new Opt(parseAtom());
    }

    private Def parseDef() throws ParserException {
	    consume(DEF);
	    return new Def(parseAtom());
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
	    return new get(parseAtom());
    }
	private Exp parseRoundPar() throws ParserException {
		consume(OPEN_PAR); // or tryNext();
		Exp exp = parseAnd();
		consume(CLOSE_PAR);
		return exp;
	}

}
