package Parser.ast;

import visitors.Visitor;

public interface AST {
	<T> T accept(Visitor<T> visitor);
}
