package onethreeseven.datastructures.data.resolver;

/**
 * Resolve the values (at the specified indices) of a string[] to double[].
 * @author Luke Bermingham
 */
public class NumericFieldsResolver extends AbstractStringArrayToFieldResolver<double[]> {

    public NumericFieldsResolver(int idx) {
        super(idx);
    }

    public NumericFieldsResolver(int... indices){
        super(indices);
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

}
