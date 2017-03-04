package onethreeseven.datastructures.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A point with some spatial dimensions
 * and one {@link LocalDateTime} dimension.
 * @author Luke Bermingham
 */
public class STPt extends CompositePt<LocalDateTime> {

    private final LocalDateTime time;

    protected STPt(double[] coords, LocalDateTime time) {
        super(coords);
        this.time = time;
    }

    @Override
    public String printExtra(String delimiter) {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public double[] getCoords(){
        return this.coords;
    }

    @Override
    public LocalDateTime getExtra() {
        return time;
    }

    public LocalDateTime getTime(){
        return time;
    }

}
