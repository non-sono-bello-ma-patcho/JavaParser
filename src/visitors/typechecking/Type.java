package visitors.typechecking;

public interface Type {
	default Type checkEqual(Type found) throws TypecheckerException {
		if (!equals(found))
			throw new TypecheckerException(found.toString(), toString());
		return this;
	}

	default Type getListElemType() throws TypecheckerException {
		if (!(this instanceof ListType))
			throw new TypecheckerException(toString(), ListType.TYPE_NAME);
		return ((ListType) this).getElemType();
	}

}
