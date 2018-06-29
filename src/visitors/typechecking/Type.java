package visitors.typechecking;

import Parser.ast.OptLiteral;

public interface Type {
	default Type checkEqual(Type found) throws TypecheckerException {
		if (!equals(found))
			throw new TypecheckerException(found.toString(), toString());
		return this;
	}
	default Type  checkOpt() throws TypecheckerException{
        if (!(this instanceof OptType))
            throw new TypecheckerException(toString(),"OPT");
        return ((OptType) this).getElemType();
    }

	default Type getListElemType() throws TypecheckerException {
		if (!(this instanceof ListType))
			throw new TypecheckerException(toString(), ListType.TYPE_NAME);
		return ((ListType) this).getElemType();
	}

}
