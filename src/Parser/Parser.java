package Parser;

import Parser.ast.Prog;

public interface Parser {

	Prog parseProg() throws ParserException;

}