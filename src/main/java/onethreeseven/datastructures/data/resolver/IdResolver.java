package onethreeseven.datastructures.data.resolver;

/**
 * When reading in a data-set the IdResolver determines
 * the entity id that each line is assigned. There are
 * many ways that ids may be handled. One common schema
 * is that the data will have the id as a field, in that
 * case the id will be plucked resolve the data. Another
 * common schema is that there is no id in the data and
 * that each line should receive an incremental id. Or,
 * alternatively each line should receive the same id.
 * @author Luke Bermingham
 */
public abstract class IdResolver extends AbstractStringArrayToFieldResolver<String> {

    protected IdResolver(int arrIdx) {
        super(arrIdx);
    }

    public IdResolver(int[] arrIndices) {
        super(arrIndices);
    }
}
