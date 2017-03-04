package onethreeseven.datastructures.data.resolver;

import onethreeseven.common.util.TimeUtil;

import java.time.LocalDateTime;

/**
 * Using elements of a string[] (given the indices) this class can formulate a {@link java.time.LocalDateTime}.
 * @author Luke Bermingham
 */
public class TemporalFieldResolver extends AbstractStringArrayToFieldResolver<LocalDateTime> {

    private final StringBuilder sb;

    public TemporalFieldResolver(int... indices) {
        super(indices);
        this.sb = new StringBuilder();
    }

    @Override
    public LocalDateTime resolve(String[] in) {
        String combinedTimeStamps = combineTimeStamps(in);
        return TimeUtil.parseDate(combinedTimeStamps);
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


}
