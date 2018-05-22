package Parser.ast;

import static java.util.Objects.requireNonNull;

import visitors.Visitor;

public class SimpleIdent implements Ident {
	private final String name;

	public SimpleIdent(String name) {
		this.name = requireNonNull(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Ident))
			return false;
		return name.equals(((Ident) obj).getName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + name + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIdent(name); // left for uniformity, instead of return visitor.visitIdent(this);
	}
}
