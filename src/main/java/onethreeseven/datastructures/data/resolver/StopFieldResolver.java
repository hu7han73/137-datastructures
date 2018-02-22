package onethreeseven.datastructures.data.resolver;

/**
 * Resolve the string "STOPPED" to true and anything else to false.
 * @author Luke Bermingham
 */
public class StopFieldResolver extends AbstractStringArrayToFieldResolver<Boolean> {

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
        super(arrIdx);
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
    public String getCommandParamString() {
        return " -s " + resolutionIndices[0];
    }

    @Override
    public Boolean resolve(String[] in) {
        String stopMoveStr = in[resolutionIndices[0]].trim().toUpperCase();
        return stopMoveStr.equals(isStoppedString);
    }

    @Override
    public String toString(){
        return "Stop/Move";
    }

}
