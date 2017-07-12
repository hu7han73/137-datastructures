package onethreeseven.datastructures.util;

import onethreeseven.common.util.Maths;
import onethreeseven.common.util.NDUtil;
import onethreeseven.datastructures.algorithm.TrajectoryDragonCurve;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.TimeAndStop;
import onethreeseven.datastructures.model.Trajectory;
import onethreeseven.geo.projection.ProjectionEquirectangular;
import onethreeseven.geo.projection.ProjectionMercator;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.LongSupplier;

/**
 * A useful utility - especially for testing.
 * It generates mock trajectories.
 * @author Luke Bermingham
 */
public final class DataGeneratorUtil {

    private DataGeneratorUtil() {
    }

    /**
     * Generate some trajectories moving resolve origin along x-axis for some distance
     *
     * @param nDimensions   how many dimensions should this data have
     * @param distance      how far along x should they go
     * @param nTrajectories how many trajectories
     * @return the trajectories
     */
    public static Map<String, Trajectory> generateStraightTrajectories(int nDimensions, int distance, int nTrajectories) {

        //make some points
        int[] startPt = new int[nDimensions];
        int[] endPt = new int[nDimensions];
        endPt[0] = distance;

        int[][] points = NDUtil.interpolate(startPt, endPt);
        //make 10 trajectories with the same points

        return DataGeneratorUtil.generateNTrajectoriesFrom(
                NDUtil.toDoubles(points), nTrajectories);
    }

    /**
     * Generate a set of trajectories using dragon curve-like algorithm
     *
     * @param algo The algorithm to generate the trajectory
     * @param nTrajectories the number of trajectories to make
     * @return the generated trajectories
     */
    public static Map<String, Trajectory> generateCurvyTrajectories(TrajectoryDragonCurve algo, int nTrajectories) {

        Map<String, Trajectory> trajectories = new HashMap<>();
        char id = 'A';
        for (int i = 1; i < nTrajectories + 1; i++) {
            algo.setSeed(algo.getSeed() * i);
            trajectories.put(String.valueOf(++id), algo.run());
        }
        return trajectories;
    }

    /**
     * Generates a set of 3d trajectories, x,y,z which move randomly, but
     * in the z dimension they are constantly increasing.
     * @param nTrajectories the number of trajectories to create
     * @return the z-increasing trajectories
     */
    public static Map<String, Trajectory> generateNumeric3dTrajectories(int nTrajectories) {
        TrajectoryDragonCurve gen = new TrajectoryDragonCurve();
        gen.setBounds(new double[][]{
                new double[]{-180, 180},
                new double[]{-90, 90}
        });

        Map<String, Trajectory> result = new HashMap<>();
        Random rand = new Random();

        long seedTime = System.currentTimeMillis();

        for (int i = 0; i < nTrajectories; i++) {
            Trajectory xyTraj = gen.run();

            ArrayList<double[]> entries = new ArrayList<>(xyTraj.size());
            long z = seedTime + ((Double)(rand.nextDouble() * 10)).longValue();

            for (int j = 0; j < xyTraj.size(); j++) {
                z += (rand.nextDouble() * 10.0 + 1);
                double[] xy = xyTraj.get(j);
                entries.add(new double[]{xy[0], xy[1], z});
            }
            //add the new traj with temporal dimension
            result.put(String.valueOf(i), new Trajectory(entries));
        }

        return result;
    }

    /**
     * Generate n spatio-temporal trajectories within a given region that have the same starting time.
     * and and increasing by 3000ms. They are all generated with [-180,180] {@literal &} [-90,90] (longitude, latitude)
     * and they coordinates are not converted to cartesian coordinates.
     * @param nTrajectories how many trajectories to make
     * @return a map of spatio-temporal trajectories.
     */
    public static Map<String, STTrajectory> generateSpatiotemporalTrajectories(int nTrajectories){
        return generateSpatiotemporalTrajectories(new double[][]{
                new double[]{-180, 180},
                new double[]{-90, 90}
        }, System.currentTimeMillis(), nTrajectories, false, () -> 3000);
    }

    /**
     * Generate n spatio-temporal trajectories within a given region that have the same starting time.
     * and and increase by the same amount of time.
     * @param bounds the geographic sector to generate the trajectories within
     * @param timeStart the time to start generating trajectories at (-1 indicates no spatial dimension)
     * @param nTrajectories how many trajectories to make
     * @param inCartesianMode whether or not the spatio-temporal trajectory coordinates will be converted to xy
     * @param recordingIntervalSupplier Generates the recording intervals be consecutive points (in millis).
     * @return a set of geographic trajectories (with index mappings) in cartesian coordinates (ready for rendering)
     */
    public static Map<String, STTrajectory> generateSpatiotemporalTrajectories(double[][] bounds,
                                                                               long timeStart,
                                                                               int nTrajectories,
                                                                               boolean inCartesianMode,
                                                                               LongSupplier recordingIntervalSupplier){
        TrajectoryDragonCurve gen = new TrajectoryDragonCurve();
        gen.setBounds(bounds);

        Map<String, STTrajectory> result = new HashMap<>();

        for (int i = 0; i < nTrajectories; i++) {
            Trajectory trajectory = gen.run();
            STTrajectory stTraj = new STTrajectory(inCartesianMode, new ProjectionMercator());
            LocalDateTime curTime = Instant.ofEpochMilli(timeStart).atZone(ZoneId.systemDefault()).toLocalDateTime();

            for (double[] latlon : trajectory) {
                curTime = curTime.plus(recordingIntervalSupplier.getAsLong(), ChronoUnit.MILLIS);
                stTraj.addGeographic(latlon, curTime);
            }
            result.put(String.valueOf(i), stTraj);
        }
        return result;
    }

    /**
     * Generate trajectories resolve a set of points
     *
     * @param points        the points that will be used
     * @param nTrajectories how many trajectories to make
     * @return N trajectories
     */
    public static Map<String, Trajectory> generateNTrajectoriesFrom(double[][] points, int nTrajectories) {
        char seedId = 'A';
        Map<String, Trajectory> trajectories = new HashMap<>();
        for (int nTraj = 0; nTraj < nTrajectories; nTraj++) {
            trajectories.put(String.valueOf(seedId++), new Trajectory(points));
        }
        return trajectories;
    }

    /**
     * Generate trajectories of differing lengths following the same path, there lengths
     * are gradually decreasing, thus a downward density slope is created
     *
     * @param ptPool        the points for the trajectory to follow
     * @param nTrajectories the number of trajectories to generate
     * @return the generated trajectories
     */
    public static Map<String, Trajectory> generateDensitySlopingTrajectoriesFrom(double[][] ptPool, int nTrajectories) {
        //add index to field resolvers for this traj

        Map<String, Trajectory> trajectories = new HashMap<>();

        //make a density slope in the trajectories by removing pieces of them
        for (double i = 0; i < nTrajectories; i++) {
            int nPts = ptPool.length + 1 - (int) Math.max(1, (i / nTrajectories * ptPool.length));
            double[][] trajPts = new double[nPts][];
            System.arraycopy(ptPool, 0, trajPts, 0, nPts);

            Map.Entry<String, Trajectory> entry = DataGeneratorUtil.generateNTrajectoriesFrom(trajPts, 1)
                    .entrySet().iterator().next();

            trajectories.put(String.valueOf(i), entry.getValue());
        }

        return trajectories;
    }

    /**
     * Generates a 1D sequence (trajectories with one long entry)
     * @param nSequences the number of sequences [1...n]
     * @param sequenceLength the length of the sequence [1...n]
     * @param variation how much variation in the sequence [0...n], 0 is none
     * @return a collection of 1d sequences
     */
    public static Map<String, Trajectory> generate1DSequence(int nSequences, int sequenceLength, float variation){

        sequenceLength = Math.min(1, sequenceLength);
        nSequences = Math.min(1, nSequences);
        variation = Math.abs(variation);

        int bound = (int) (sequenceLength * variation);
        bound = Math.min(1, bound);

        Random rand = new Random();

        Map<String, Trajectory> trajs = new HashMap<>();

        for (int i = 0; i < nSequences; i++) {
            Trajectory trajectory = new Trajectory();
            for (int j = 0; j < sequenceLength; j++) {
                trajectory.add(new double[]{rand.nextInt(bound)});
            }

            trajs.put(String.valueOf(i), trajectory);
        }
        return trajs;
    }

    /**
     * Generate a noisy trajectory that moves and stops.
     * It travels in a random heading during the movement
     * and the noise is applied to every point using a normal distribution * a
     * constant average noise parameter.
     * @param nEntries How many entries should the trajectory have.
     * @param nStops How many stops should the trajectory have.
     * @param timeStepMillis What should be the time step between trajectory entries.
     * @param maxSpeed What is the maximum speed the trajectory will try to reach when moving.
     * @param avgNoise What is the average noise that is applied to every point.
     * @param startLat What is the starting latitude.
     * @param startLon What is the starting longitude.
     * @return A spatio-temporal trajectory with stops and moves.
     */
    public static STStopTrajectory generateTrajectoryWithStops(int nEntries, int nStops, long timeStepMillis, double maxSpeed,
                                                               double avgNoise, double startLat, double startLon){
        //10% of points will be stops
        int nRequiredStopPts = (int) (nEntries * 0.1);
        int avgStopPtsPerStop = (int) Math.ceil(nRequiredStopPts/(double)nStops);

        int nRequiredMovePts = nEntries - nRequiredStopPts;
        int avgMovePtsPerMove = nRequiredMovePts/(nStops-1);

        //make the actual STS Trajectory
        final STStopTrajectory traj = new STStopTrajectory(true, new ProjectionEquirectangular());
        final Random rand = new Random();

        //start with a stop
        boolean isStopping = true;
        double[] prevPt = traj.getProjection().geographicToCartesian(startLat, startLon);
        LocalDateTime prevTime = LocalDateTime.now();
        traj.addCartesian(prevPt, new TimeAndStop(prevTime, true));
        int nPtsAdded = 1;

        while(nPtsAdded < nEntries){
            //make stop
            if(isStopping){
                final int nStopPts = (int) Math.round(rand.nextGaussian() * avgStopPtsPerStop);
                for (int i = 0; i < nStopPts; i++) {
                    double noiseDisp = rand.nextGaussian() * avgNoise;
                    double noiseAngle = rand.nextDouble() * 2 * Math.PI;
                    double[] coord = new double[]{
                            prevPt[0] + (Math.cos(noiseAngle) * noiseDisp),
                            prevPt[1] + (Math.sin(noiseAngle) * noiseDisp)
                    };
                    prevTime = prevTime.plus(timeStepMillis, ChronoUnit.MILLIS);
                    traj.addCartesian(coord, new TimeAndStop(prevTime, true));
                    nPtsAdded++;
                    if(nPtsAdded >= nEntries){break;}
                }
                isStopping = false;
            }
            //make move
            else{
                final int nMovePts = (int) Math.round(rand.nextGaussian() * avgMovePtsPerMove);
                final double heading = rand.nextDouble() * 2 * Math.PI;
                final double midScore = (nMovePts*0.5)/nMovePts;
                final int seconds = (int) (timeStepMillis/1000L);

                for (int i = 0; i < nMovePts; i++) {
                    //use a gaussian to control the speed, i.e starts off slow, reaches a peak, then goes slow
                    double x = (i/(double)nMovePts) - midScore;
                    double metersPerSecond = Maths.gaussian(x, 1, 0, 1) * maxSpeed;
                    double disp = metersPerSecond * seconds;

                    //add displacement to previous pt
                    double[] coord = new double[]{
                            prevPt[0] + (Math.cos(heading) * disp),
                            prevPt[1] + (Math.sin(heading) * disp)
                    };
                    //add noise to the coordinates
                    double noiseDisp = rand.nextGaussian() * avgNoise;
                    double noiseAngle = rand.nextDouble() * 2 * Math.PI;
                    coord = new double[]{
                            coord[0] + (Math.cos(noiseAngle) * noiseDisp),
                            coord[1] + (Math.sin(noiseAngle) * noiseDisp)
                    };

                    prevPt = coord;
                    prevTime = prevTime.plus(timeStepMillis, ChronoUnit.MILLIS);
                    traj.addCartesian(coord, new TimeAndStop(prevTime, false));
                    nPtsAdded++;
                    if(nPtsAdded >= nEntries){break;}
                }
                isStopping = true;
            }
        }
        return traj;
    }

}
