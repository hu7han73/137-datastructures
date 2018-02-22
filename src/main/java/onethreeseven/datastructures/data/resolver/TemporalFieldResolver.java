package onethreeseven.datastructures.data.resolver;

import onethreeseven.common.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * Using elements of a string[] (given the indices) this class can formulate a {@link java.time.LocalDateTime}.
 * @author Luke Bermingham
 */
public class TemporalFieldResolver extends AbstractStringArrayToFieldResolver<LocalDateTime> {

    private final StringBuilder sb;

    private final Function<String, LocalDateTime> textToDateTimeParser;

    /**
     * Uses a custom string to date-time function to resolve a combined string.
     * @param customResolver The resolver function to use.
     * @param indices The indices of the strings to combine together into a date-time string to parse.
     */
    public TemporalFieldResolver(Function<String, LocalDateTime> customResolver, int... indices){
        super(indices);
        this.textToDateTimeParser = customResolver;
        this.sb = new StringBuilder();
    }

    /**
     * Calls {@link TemporalFieldResolver#TemporalFieldResolver(Function, int...)} and passes
     * {@link TimeUtil#parseDate(String)} as the custom resolver.
     * @param indices The indices of the strings to combine together into a date-time string to parse.
     */
    public TemporalFieldResolver(int... indices) {
        this(TimeUtil::parseDate, indices);
    }

    @Override
    public String getCommandParamString() {
        StringBuilder sb = new StringBuilder(" -t ");
        for (int i = 0; i < resolutionIndices.length; i++) {
            sb.append(String.valueOf(resolutionIndices[i]));
            if(i != resolutionIndices.length - 1){
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    @Override
    public LocalDateTime resolve(String[] in) {
        String combinedTimeStamps = combineTimeStamps(in);
        return this.textToDateTimeParser.apply(combinedTimeStamps);
    }

    private String combineTimeStamps(String[] timeStampParts){
        sb.setLength(0);
        int len = resolutionIndices.length;
        int lastIdx = len-1;
        for (int i = 0; i < resolutionIndices.length; i++) {
            int idx = resolutionIndices[i];
            sb.append(timeStampParts[idx]);
            if(i < lastIdx){
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public String toString(){
        return "Time";
    }

}
