package onethreeseven.datastructures.model;

import onethreeseven.geo.projection.AbstractGeographicProjection;

import java.time.LocalDateTime;

/**
 * Trajectory with space, time, and speed.
 * @author Luke Bermingham
 */
public class STStopTrajectory extends SpatioCompositeTrajectory<STStopPt> {

    public STStopTrajectory(){
        super();
    }

    public STStopTrajectory(boolean inCartesianMode, AbstractGeographicProjection projection){
        super(inCartesianMode, projection);
    }

    public boolean isStopped(int i){
        return get(i).isStopped();
    }

    public LocalDateTime getTime(int i){
        return get(i).getTime();
    }

    public void addCartesian(double[] coords, TimeAndStop timeAndStop) {
        super.addCartesian(new STStopPt(coords, timeAndStop));
    }

    public void addGeographic(double[] coords, TimeAndStop timeAndStop){
        super.addGeographic(new STStopPt(coords, timeAndStop));
    }

}
