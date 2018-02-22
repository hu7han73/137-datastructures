package onethreeseven.datastructures.data.resolver;

/**
 * For resolving latitude.
 * @author Luke Bermingham
 */
public class LatFieldResolver extends AbstractStringArrayToFieldResolver<Double> {

    public LatFieldResolver(int latIdx) {
        super(latIdx);
    }

    @Override
    public String getCommandParamString() {
        throw new UnsupportedOperationException("Lat field by itself has not logical command string to provide. You should use the NumericFieldResolver instead.");
    }

    @Override
    public Double resolve(String[] in) {
        String s = in[this.resolutionIndices[0]];
        return Double.parseDouble(s.trim());
    }

    @Override
    public String toString(){
        return "Latitude";
    }

}
