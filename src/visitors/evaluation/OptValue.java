package visitors.evaluation;

public class OptlValue  extends PrimValue<Value>  {

    public OptlValue(Value value) {
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
        return value; //ricordarsi poi di fare cast quando chiamo il metodo. narrowing
    }


}