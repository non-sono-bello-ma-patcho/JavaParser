package visitors.typechecking;

import static java.util.Objects.requireNonNull;

public class OptType implements Type {

    private final Type elemType;

    public OptType(Type elemType) {
        this.elemType = requireNonNull(elemType);
    }

    public Type getElemType() {
        return this.elemType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof OptType))
            return false;
        return elemType.equals(((OptType) obj).elemType);
    }

    @Override
    public int hashCode() {
        return elemType.hashCode();
    }

    @Override
    public String toString() {
        return elemType.toString() + " OPT";
    }
}
