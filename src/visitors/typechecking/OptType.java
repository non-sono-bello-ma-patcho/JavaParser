package visitors.typechecking;

import static java.util.Objects.requireNonNull;

public class OptType implements Type {

    private final Type elemType;
    private  boolean empty=false;
    public  String TypeName;

    public OptType(Type elemType) {
        this.elemType = requireNonNull(elemType);
        this.TypeName=elemType.toString();
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
        return elemType.equals(obj);
    }

    @Override
    public int hashCode() {
        return elemType.hashCode();
    }

    @Override
    public String toString() {
        return TypeName + " OPT";
    }
}
