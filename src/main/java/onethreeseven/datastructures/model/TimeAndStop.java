package onethreeseven.datastructures.model;

import onethreeseven.datastructures.data.resolver.ResolverConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Container for both time and stop
 * @author Luke Bermingham
 */
public class TimeAndStop {

    private final LocalDateTime time;
    private boolean isStopped;

    public TimeAndStop(LocalDateTime time, boolean isStop) {
        this.time = time;
        this.isStopped = isStop;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public String print(String delimiter){
        String timeStamp = time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return timeStamp + delimiter + ((isStopped) ?
                ResolverConstants.STOPPED : ResolverConstants.MOVING);
    }

    @Override
    public String toString() {
        return print(",");
    }
}
