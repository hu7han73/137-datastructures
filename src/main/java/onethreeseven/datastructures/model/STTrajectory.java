package onethreeseven.datastructures.model;

import onethreeseven.geo.projection.AbstractGeographicProjection;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * A trajectory with spatial and one temporal dimension.
 * @author Luke Bermingham
 */
public class STTrajectory extends SpatioCompositeTrajectory<STPt> {

    public STTrajectory(boolean inCartesianMode, AbstractGeographicProjection projection){
        //lat lon
        super(inCartesianMode, projection);
    }

    public STTrajectory(){
        super();
    }

    public LocalDateTime getTime(int i){
        return this.entries.get(i).getTime();
    }

    /**
     * Given index i and i-1 compute the speed between them.
     * @param i The index to find the speed for.
     * @return The speed in meters per second at the current index.
     */
    public double getSpeed(int i){
        if(i == 0){
            return 0.0;
        }
        long deltaTimeMillis = ChronoUnit.MILLIS.between(getTime(i-1), getTime(i));
        double dist = getEuclideanDistance(i, i-1);
        return dist/(deltaTimeMillis/1000.0);
    }

    public void addGeographic(double[] coords, LocalDateTime time) {
        this.addGeographic(new STPt(coords, time));
    }
}
