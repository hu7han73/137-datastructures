package onethreeseven.datastructures.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Represents a n-dimensional trajectory made of numerical points.
 * For a trajectory containing spatial data refer to {@link SpatialTrajectory}.
 */
public class Trajectory implements ITrajectory<double[]> {

    protected final ArrayList<double[]> entries;
    private double[][] bounds = null;

    public Trajectory(){
        this.entries = new ArrayList<>();
    }

    public Trajectory(ArrayList<double[]> entries){
        this.entries = new ArrayList<>(entries);
    }

    public Trajectory(double[][] arr) {
        this.entries = new ArrayList<>(arr.length);
        Collections.addAll(entries, arr);
    }

    /**
     * Create an empty trajectory of a given size,
     * this is useful for dynamically creating the trajectory (such as resolve geographic coordinates).
     *
     * @param size       the size of the trajectory when it is complete
     */
    public Trajectory(int size) {
        this.entries = new ArrayList<>(size);
    }

    /**
     * @return Gets the bounds of the trajectory across n-dimensions.
     * Note: after the first call to this getter the result is cached because its o(n).
     */
    @Override
    public double[][] getBounds() {
        //return the caches bounds if calculated
        if (bounds != null) {
            return bounds;
        }
        //cache it for future
        bounds = ITrajectory.super.getBounds();
        return bounds;
    }

    @Override
    public double[] get(int idx) {
        return this.entries.get(idx);
    }

    @Override
    public Iterator<double[]> coordinateIter() {
        return iterator();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Iterator<double[]> iterator() {
        return this.entries.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trajectory)) return false;

        Trajectory that = (Trajectory) o;

        return that.size() == this.size() && this.entries.equals(that.entries);

    }

    @Override
    public void add(double[] entry) {
        this.entries.add(entry);
    }
}
