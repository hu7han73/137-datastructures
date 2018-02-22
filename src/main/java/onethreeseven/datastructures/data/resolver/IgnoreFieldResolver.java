package onethreeseven.datastructures.data.resolver;

/**
 * Ignore some field when resolving text to object types.
 * @author Luke Bermingham
 */
public class IgnoreFieldResolver extends AbstractStringArrayToFieldResolver<Void> {

    public IgnoreFieldResolver(int arrIdx) {
        super(arrIdx);
    }

    @Override
    public String getCommandParamString() {
        throw new UnsupportedOperationException("Ignore field should not be passed into a load trajectory command.");
    }

    @Override
    public Void resolve(String[] in) {
        return null;
    }

    @Override
    public String toString(){
        return "Ignored";
    }

}
