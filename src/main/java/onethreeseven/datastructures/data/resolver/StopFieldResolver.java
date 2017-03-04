package onethreeseven.datastructures.data.resolver;

/**
 * Resolve the string "STOPPED" to true and anything else to false.
 * @author Luke Bermingham
 */
public class StopFieldResolver implements IResolver<String[], Boolean> {

    private final int arrIdx;

    public StopFieldResolver(int arrIdx) {
        this.arrIdx = arrIdx;
    }

    @Override
    public Boolean resolve(String[] in) {
        String stopMoveStr = in[arrIdx].trim().toUpperCase();
        return stopMoveStr.equals(ResolverConstants.STOPPED);
    }
}
