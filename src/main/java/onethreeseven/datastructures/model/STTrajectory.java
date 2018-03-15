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

    public double getMinSpeed(){
        double minSpeed = Double.MAX_VALUE;
        for (int i = 0; i < size(); i++) {
            double curSpeed = getSpeed(i);
            if(curSpeed < minSpeed){
                minSpeed = curSpeed;
            }
        }
        return minSpeed;
    }

    public double getMaxSpeed(){
        double maxSpeed = Double.MIN_VALUE;
        for (int i = 0; i < size(); i++) {
            double curSpeed = getSpeed(i);
            if(curSpeed > maxSpeed){
                maxSpeed = curSpeed;
            }
        }
        return maxSpeed;
    }

    public double getAverageSpeed(){
        double avgSpeed = 0;
        for (int i = 0; i < size(); i++) {
            double curSpeed = getSpeed(i);
            avgSpeed += curSpeed;
        }
        return avgSpeed / size();
    }

    /**
     * Gets the duration of this trajectory by checking its first and last entries.
     * @param timeUnit The unit of time get the duration in.
     * @return The duration of trajectory as defined above.
     */
    public long getDuration(ChronoUnit timeUnit){
        int trajSize = size();

        if(trajSize <= 1){
            throw new IllegalArgumentException("Trajectory must have at least two entries.");
        }
        LocalDateTime startTime = getTime(0);
        LocalDateTime endTime = getTime(trajSize-1);
        return timeUnit.between(startTime, endTime);
    }

    public void addCartesian(double[] coords, LocalDateTime time){
        this.addCartesian(new STPt(coords, time));
    }

    public void addGeographic(double[] latlon, LocalDateTime time) {
        this.addGeographic(new STPt(latlon, time));
    }

    @Override
    public String toString(){
        return "Spatio-temporal Trajectory (" + size() + " entries)";
    }

}
