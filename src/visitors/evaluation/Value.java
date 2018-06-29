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

	default boolean asBool() { throw new EvaluatorException("Expecting a bool value");}

	default OptValue asOpt(){ throw new EvaluatorException("Expecting an Opt value");}

}
