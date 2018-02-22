package onethreeseven.datastructures.data.resolver;

/**
 * As each line is read in it is assigned an incremental id.
 * @author Luke Bermingham
 */
public class IncrementalIdResolver extends IdResolver {

    private int id = 0;

    public IncrementalIdResolver() {
        super(-1);
    }

    @Override
    public String resolve(String[] fields) {
        String res = String.valueOf(id);
        id++;
        return res;
    }

    @Override
    public String getCommandParamString() {
        return " -id ++";
    }
}
