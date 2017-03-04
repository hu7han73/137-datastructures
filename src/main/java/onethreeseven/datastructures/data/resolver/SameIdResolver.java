package onethreeseven.datastructures.data.resolver;

/**
 * All lines that are read in are assigned the same id.
 * @author Luke Bermingham
 */
public class SameIdResolver extends IdResolver {

    private final String id;

    /**
     * Using this constructor all ids are resolved to the string "0".
     */
    public SameIdResolver(){
        this("0");
    }

    public SameIdResolver(String id){
        super(-1);
        this.id = id;
    }

    @Override
    public String resolve(String[] fields) {
        return id;
    }

    public String getId() {
        return id;
    }
}
