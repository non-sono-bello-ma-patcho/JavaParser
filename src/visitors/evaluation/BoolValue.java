package visitors.evaluation;

public class BoolValue  extends PrimValue<Boolean>  {

    public BoolValue(Boolean value) {
        super(value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Boolean))
            return false;
        return value.equals(((Boolean) obj).booleanValue());
    }

    @Override
    public boolean asBool() {
        return value;
    }


}
