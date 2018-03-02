package onethreeseven.datastructures.model;

import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;

/**
 * Interface for the various kinds of trajectories.
 * @param <T> The type of entries in this trajectory.
 * @author Luke Bermingham
 */
public interface ITrajectory<T> extends BoundingCoordinates, Iterable<T> {

    /**
     * Get the entry at the specified index.
     * @param idx The index of the entry to get.
     * @return The entry at the index.
     */
    T get(int idx);

    /**
     * Adds an entry to this trajectory.
     * @param entry The entry to add.
     */
    void add(T entry);

    /**
     * @return The size of the trajectory.
     */
    int size();

}
