package onethreeseven.datastructures.data.resolver;


/**
 * Using provided indices resolve n Strings resolve a String[] to some type T
 * @param <T> The type to resolve to.
 * @author Luke Bermingham.
 */
public abstract class AbstractStringArrayToFieldResolver<T> implements IResolver<String[], T> {

    final int[] resolutionIndices;

    protected AbstractStringArrayToFieldResolver(int arrIdx) {
        this(new int[]{arrIdx});
    }

    protected AbstractStringArrayToFieldResolver(int[] arrIndices){
        if(arrIndices.length < 1){
            throw new IllegalArgumentException("Must have at least one index.");
        }
        this.resolutionIndices = arrIndices;
    }

}
