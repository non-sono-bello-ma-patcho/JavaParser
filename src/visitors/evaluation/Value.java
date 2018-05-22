package visitors.evaluation;

public interface Value {
	/* default conversion methods */
	default int asInt() {
		throw new EvaluatorException("Expecting an integer value");
	}

	default String asString() {
		throw new EvaluatorException("Expecting a string value");
	}

	default ListValue asList() {
		throw new EvaluatorException("Expecting a list value");
	}

}
