package Parser;

import visitors.evaluation.Value;

import static Parser.TokenType.*;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class StreamTokenizer implements Tokenizer {
	private static final String regEx;
	private static final Map<String, TokenType> keywords = new HashMap<>();
	private static final Map<String, TokenType> symbols = new HashMap<>();

	private boolean hasNext = true; // any stream contains at least the EOF
									// token
	private TokenType tokenType;
	private String tokenString;
	private int intValue;
	private boolean boolValue;
	private final Scanner scanner;

	static {
		// remark: groups must correspond to the ordinal of the corresponding
		// token type
		final String identRegEx = "([a-zA-Z][a-zA-Z0-9]*)"; // group 1
		final String numRegEx = "(0[bB][01]+) | ([1-9][0-9]*|0)"; // group 2   new regex or num regex???????
		final String skipRegEx = "(\\s+|//.*)"; // group 3
		final String symbolRegEx = "\\+|\\*|!|==|=|&&|\\(|\\)|;|,|\\{|\\}|-|::|:|\\[|\\]"; //4
        /* forse bisogna aggiungere qui la regex per le binary expr*/
		regEx = identRegEx + "|" + numRegEx  + "|" + skipRegEx + "|" + symbolRegEx;
	}
/*
operatori unari prefissi ! , opt , empty , def e get Tutti gli operatori unari prefissi hanno la precedenza sugli operatori
binari infissi.*/


	static {
		keywords.put("for", FOR);
		keywords.put("print", PRINT);
		keywords.put("var", VAR);
		/*parte aggiunta da me */
		keywords.put("true",BOOLEAN); // TODO: check bin operation (typecheck)
		keywords.put("false",BOOLEAN); // TODO: same thing as above
		keywords.put("opt",OPT);
		keywords.put("empty",EMPTY);
		keywords.put("get",GET);
		keywords.put("def",DEF);
		keywords.put("if",IF);
		keywords.put("else",ELSE);
		keywords.put("do",DO);
		keywords.put("while",WHILE);
	}

	static {
		symbols.put("+", PLUS);
		symbols.put("*", TIMES);
		symbols.put("::", PREFIX);
		symbols.put("=", ASSIGN);
		symbols.put(":", IN);
		symbols.put("(", OPEN_PAR);
		symbols.put(")", CLOSE_PAR);
		symbols.put(";", STMT_SEP);
		symbols.put(",", EXP_SEP);
		symbols.put("{", OPEN_BLOCK);
		symbols.put("}", CLOSE_BLOCK);
		symbols.put("-", MINUS);
		symbols.put("[", OPEN_LIST);
		symbols.put("]", CLOSE_LIST);
		/*parte aggiunta da me*/
		symbols.put("==", EQUAL);
		symbols.put("&&", AND);
	}

	public StreamTokenizer(Reader reader) {
		scanner = new StreamScanner(regEx, reader);
	}

	private void checkType() {
		tokenString = scanner.group();

		System.err.println("Tokenstring is: "+tokenString+"Tokentype is: "+symbols.get(tokenString));

		if (scanner.group(IDENT.ordinal()) != null) { // IDENT or a keyword
			tokenType = keywords.get(tokenString);
			if (tokenType == null)
				tokenType = IDENT;
			if(tokenType == BOOLEAN)
				boolValue = Boolean.parseBoolean(tokenString);
			return;
		}
		if (scanner.group(NUM.ordinal()) != null) { // NUM
			tokenType = NUM;

			intValue = Integer.parseInt(base()==2?tokenString : tokenString.substring(2), base());
			return;
		}

		if (scanner.group(SKIP.ordinal()) != null) { // SKIP
			tokenType = SKIP;
			return;
		}

		tokenType = symbols.get(tokenString); // a symbol
		if (tokenType == null)
			throw new AssertionError("Fatal error");
	}

	private int base(){
		return tokenString.indexOf('b')>=0 || tokenString.indexOf('B')>=0 ? 2 : 10;
	}

	@Override
	public TokenType next() throws TokenizerException {
		do {
			tokenType = null;
			tokenString = "";
			try {
				if (hasNext && !scanner.hasNext()) {
					hasNext = false;
					return tokenType = EOF;
				}
				scanner.next();
			} catch (ScannerException e) {
				throw new TokenizerException(e);
			}
			checkType();
		} while (tokenType == SKIP);
		return tokenType;
	}

	private void checkValidToken() {
		if (tokenType == null)
			throw new IllegalStateException();
	}

	private void checkValidToken(TokenType ttype) {
		if (tokenType != ttype)
			throw new IllegalStateException();
	}

	@Override
	public String tokenString() {
		checkValidToken();
		return tokenString;
	}

	@Override
	public int intValue() {
		checkValidToken(NUM);
		return intValue;
	}

	@Override
    public boolean boolValue(){
	    checkValidToken(BOOLEAN);
	    return boolValue;
	}

	@Override
	public TokenType tokenType() {
		checkValidToken();
		return tokenType;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public void close() throws TokenizerException {
		try {
			scanner.close();
		} catch (ScannerException e) {
			throw new TokenizerException(e);
		}
	}
}
