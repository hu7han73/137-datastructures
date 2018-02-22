package onethreeseven.datastructures.data.resolver;

/**
 * Uses one or more fields resolve an input line as a single id.
 * If multiple indices are specified the field values are appended
 * in index order.
 * @author Luke Bermingham
 */
public class IdFieldResolver extends IdResolver {

    private final StringBuilder sb;

    public IdFieldResolver(int arrIdx){
        this(new int[]{arrIdx});
    }

    public IdFieldResolver(int[] arrIndices) {
        super(arrIndices);
        this.sb = (arrIndices.length > 1) ? new StringBuilder() : null;
    }

    @Override
    public String getCommandParamString() {
        StringBuilder sb = new StringBuilder(" -id ");
        for (int i = 0; i < resolutionIndices.length; i++) {
            sb.append(String.valueOf(resolutionIndices[i]));
            if(i != resolutionIndices.length - 1){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public String resolve(String[] fields) {
        if(resolutionIndices.length > 1){
            for (int resolutionIndice : resolutionIndices) {
                sb.append(fields[resolutionIndice]);
            }
            String res = sb.toString().trim();
            sb.setLength(0);
            return res;
        }
        else{
            return fields[resolutionIndices[0]];
        }
    }

    @Override
    public String toString(){
        return "Id";
    }

}
