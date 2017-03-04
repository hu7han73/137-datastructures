package onethreeseven.datastructures.algorithm;


import onethreeseven.datastructures.model.Trajectory;
import onethreeseven.datastructures.util.BoundsUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Algorithm to generate a trajectory, inspired by the famous dragon curve algorithm.
 * @author Luke Bermingham.
 */
public class TrajectoryDragonCurve {

    /**
     * The seed used to generate the trajectory
     */
    private long seed = System.currentTimeMillis();

    /**
     * How straight the movement is along the sequence
     */
    private double straightness = 0.8;

    /**
     * How erratic the movement is
     */
    private double volatility = 0.25;
    /**
     * The complexity of the trajectory
     */
    private int complexity = 15;

    /**
     * The dimensional bounds
     */
    private double[][] bounds = new double[][]{new double[]{-1000, 1000}, new double[]{-1000, 1000}, new double[]{0, 1000}};

    /**
     * Entries per trajectory
     */
    private int limit = 1000;

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public double getStraightness() {
        return straightness;
    }

    public void setStraightness(double straightness) {
        this.straightness = straightness;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public double[][] getBounds() {
        return bounds;
    }

    public void setBounds(double[][] bounds) {
        this.bounds = bounds;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Trajectory run() {
        //the turning directions of the trajectory
        List<Integer> turns = getSequence(getComplexity(), getLimit());
        Random rand = new Random(getSeed());
        double angle = -getComplexity() * Math.PI;

        int nDimensions = getBounds().length;
        //make the points
        double[] prevPt = new double[nDimensions];

        //setup trajectory
        double[][] trajectory = new double[turns.size()][];
        double[] curPt = makeNdPoint(angle, prevPt);

        //start bounds off
        prevPt = curPt;


        for (int i = 0; i < turns.size(); i++) {
            int turn = turns.get(i);
            double randOffset = rand.nextGaussian();
            if (Math.abs(randOffset) > 1) {
                //beyond one Standard dev
                angle += turn * ((Math.PI * (getVolatility())) * randOffset);
            } else {
                angle += (Math.PI * (1 - getStraightness())) * randOffset;
            }
            //grow bounds and add point
            curPt = makeNdPoint(angle, prevPt);
            trajectory[i] = curPt;
            prevPt = curPt;
        }

        //normalise the trajectory into the given bounds
        BoundsUtil.normaliseIntoBounds(trajectory, getBounds());
        return new Trajectory(trajectory);
    }

    private double[] makeNdPoint(double angle, double[] seedPt) {
        double[] ndPt = new double[seedPt.length];
        for (int i = 0; i < seedPt.length; i++) {
            double offset = (i % 3 == 0) ? Math.tan(angle) + Math.sin(angle) : ((i % 2 == 0) ? Math.cos(angle) : Math.sin(angle));
            ndPt[i] = seedPt[i] + offset;
        }
        return ndPt;
    }

    private List<Integer> getSequence(int iterations, int limit) {
        List<Integer> turnSequence = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            List<Integer> copy = new ArrayList<>(turnSequence);
            Collections.reverse(copy);
            turnSequence.add(1);
            for (Integer turn : copy) {
                turnSequence.add(-turn);
                if (turnSequence.size() >= limit) {
                    return turnSequence;
                }
            }
        }
        return turnSequence;
    }


}
