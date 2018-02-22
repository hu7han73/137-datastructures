package onethreeseven.datastructures.data.resolver;

/**
 * Resolve the values (at the specified indices) of a string[] to double[].
 * @author Luke Bermingham
 */
public class NumericFieldsResolver extends AbstractStringArrayToFieldResolver<double[]> {

    private final String commandString;

    public NumericFieldsResolver(LatFieldResolver latFieldResolver, LonFieldResolver lonFieldResolver){
        super(new int[]{latFieldResolver.resolutionIndices[0], lonFieldResolver.resolutionIndices[0]});
        this.commandString = " -ll " + latFieldResolver.resolutionIndices[0] + " " + lonFieldResolver.resolutionIndices[0];
    }

    public NumericFieldsResolver(int idx) {
        super(idx);
        commandString = null;
    }

    public NumericFieldsResolver(int... indices){
        super(indices);
        commandString = null;
    }

    @Override
    public String getCommandParamString() {
        if(commandString == null){
            throw new IllegalStateException("Cannot make command string did not use lat lon constructor.");
        }
        return commandString;
    }

    @Override
    public double[] resolve(String[] in) {
        double[] arr = new double[resolutionIndices.length];
        int j = 0;
        for (int i : resolutionIndices) {
            String s = in[i];
            double d = Double.parseDouble(s.trim());
            arr[j] = d;
            j++;
        }
        return arr;
    }

    @Override
    public String toString(){
        return "Numeric";
    }

}
