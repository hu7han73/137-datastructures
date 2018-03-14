package onethreeseven.datastructures.util;

import onethreeseven.datastructures.model.ITrajectory;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import onethreeseven.trajsuitePlugin.util.BoundsUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calculates and outputs statistics about trajectories.
 * @author Luke Bermingham
 */
public class TrajectoryStatistician {

    private static final NumberFormat formatter = new DecimalFormat("#0.00");

    private TrajectoryStatistician(){}

    public static Map<String, String> getStats(ITrajectory traj){
        LinkedHashMap<String, String> stats = new LinkedHashMap<>();

        double[][] bounds = traj.getBounds();
        double area = BoundsUtil.getVolume(bounds);


        stats.put("Entries:" , String.valueOf(traj.size()));
        stats.put("Area(m):", formatter.format(area));

        if(traj instanceof SpatioCompositeTrajectory){
            double pathLength = ((SpatioCompositeTrajectory) traj).distanceAlong(0, traj.size()-1);
            stats.put("Path length(m):", formatter.format(pathLength));
        }
        if(traj instanceof STTrajectory){
            long durationSeconds = ((STTrajectory) traj).getDuration(ChronoUnit.SECONDS);
            stats.put("Duration(s):", String.valueOf(durationSeconds));
            double minSpeed = ((STTrajectory) traj).getMinSpeed();
            double maxSpeed = ((STTrajectory) traj).getMaxSpeed();
            double avgSpeed = ((STTrajectory) traj).getAverageSpeed();
            stats.put("Minimum speed (m/s):", formatter.format(minSpeed));
            stats.put("Maximum speed (m/s):", formatter.format(maxSpeed));
            stats.put("Average speed (m/s):", formatter.format(avgSpeed));
        }
        if(traj instanceof STStopTrajectory){
            CountStopsAndMoves stopStatistician = new CountStopsAndMoves();
            stopStatistician.run((STStopTrajectory) traj);
            stats.putAll(stopStatistician.getAllStats());
        }

        return stats;
    }



    /**
     * Prints the total size and average size of a collection of trajectories.
     * @param trajs The trajectories to print stats for.
     */
    public static void printSizeStats(Collection<? extends ITrajectory> trajs){
        int totalSize = 0;
        for (ITrajectory traj : trajs) {
            totalSize += traj.size();
        }
        System.out.println("Total trajectories: " + trajs.size());
        System.out.println("Total entries: " + totalSize);
        double avgSize = totalSize / (double)trajs.size();
        System.out.println("Average trajectory size: " + avgSize);
    }

    /**
     * Prints the total duration and average duration of trajectories from this collection.
     * @param trajs A collection of spatio-temporal trajectories.
     * @param timeUnit The time unit for the duration and average duration.
     */
    public static void printTemporalStats(Collection<STTrajectory> trajs, ChronoUnit timeUnit){
        long totalUnit = 0;
        for (STTrajectory traj : trajs) {
            totalUnit += traj.getDuration(timeUnit);
        }
        System.out.println("Total duration (" + timeUnit.name() + "): " + totalUnit);
        double avgDuration = totalUnit / (double)trajs.size();
        System.out.println("Average duration (" + timeUnit.name() + "): " + avgDuration);
    }

}
