package Parser.ast;


import visitors.evaluation.Value;


public class  OptLiteral implements  Value {


    private final Value value;
    private  boolean empty = false;

    public OptLiteral(Value value) {
        this.value = value;

    }

    public OptLiteral(OptLiteral opt) {
        this.value = opt.getValue();

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
        if (!(obj instanceof OptLiteral))
            return false;
        return (this.empty==true && ((OptLiteral)obj).empty==true)||(value.equals(((OptLiteral) obj).value) &&(this.empty==false
                && ((OptLiteral)obj).empty==false) );
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public OptLiteral asOpt() {
        return this;
    }

    @Override
    public String toString() {
        return "opt " + ((empty)? "empty" : value.toString());
    }

}
