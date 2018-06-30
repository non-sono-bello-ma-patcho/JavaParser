package visitors.evaluation;


public class OptValue  implements Value{

    private final Value value;
    private  boolean empty = false;

    public OptValue(Value value) {
        this.value = value;

    }

    public OptValue(OptValue opt) {
        this.value = opt.value;
        this.empty = opt.empty;

    }

    public Value getValue() {
        return this.value;
    }

    public void setEmpty(boolean v) {
        this.empty=v;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof OptValue))
            return false;
        return (this.empty && ((OptValue)obj).empty)||(value.equals(((OptValue) obj).value) &&(this.empty
                && !((OptValue)obj).empty));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public OptValue asOpt() {
        return this;
    }

    @Override
    public String toString() {
        return "opt " + ((empty)? "empty" : value.toString());
    }
}
