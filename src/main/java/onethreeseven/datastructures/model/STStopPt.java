package onethreeseven.datastructures.model;

import java.time.LocalDateTime;

/**
 * Point with space, time, and stop.
 * @author Luke Bermingham
 */
public class STStopPt extends STPt {

    private final TimeAndStop timeAndStop;

    protected STStopPt(double[] coords, TimeAndStop timeAndStop) {
        super(coords, timeAndStop.getTime());
        this.timeAndStop = timeAndStop;
    }

    @Override
    public String printExtra(String delimiter) {
        return timeAndStop.print(delimiter);
    }

    @Override
    public LocalDateTime getExtra() {
        return super.getExtra();
    }

    public boolean isStopped(){
        return timeAndStop.isStopped();
    }

    public void setIsStopped(boolean isStopped){
        this.timeAndStop.setStopped(isStopped);
    }

}
