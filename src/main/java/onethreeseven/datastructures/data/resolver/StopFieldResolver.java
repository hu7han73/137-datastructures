package onethreeseven.datastructures.data.resolver;

/**
 * Resolve the string "STOPPED" to true and anything else to false.
 * @author Luke Bermingham
 */
public class StopFieldResolver implements IResolver<String[], Boolean> {

    private final int arrIdx;

    /**
     * The string to that is checked against to indicate a stop.
     */
    private final String isStoppedString;


    /**
     * Creates a stop resolver where it checks the specified index of the array for a string
     * that matches the isStoppedString.
     * @param arrIdx The array index to check.
     * @param isStoppedString The isStoppedString to match against, for example: "STOPPED".
     */
    public StopFieldResolver(int arrIdx, String isStoppedString){
        this.arrIdx = arrIdx;
        this.isStoppedString = isStoppedString.toUpperCase();
    }

    /**
     * Calls {@link StopFieldResolver#StopFieldResolver(int, String)} with
     * {@link StopFieldResolver#isStoppedString} set to {@link ResolverConstants#STOPPED}.
     * @param arrIdx The array index to check.
     */
    public StopFieldResolver(int arrIdx) {
        this(arrIdx, ResolverConstants.STOPPED);
    }

    @Override
    public Boolean resolve(String[] in) {
        String stopMoveStr = in[arrIdx].trim().toUpperCase();
        return stopMoveStr.equals(ResolverConstants.STOPPED);
    }
}
