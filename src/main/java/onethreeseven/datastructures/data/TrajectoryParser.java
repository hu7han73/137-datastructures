package onethreeseven.datastructures.data;


import onethreeseven.datastructures.data.resolver.IdFieldResolver;
import onethreeseven.datastructures.data.resolver.IdResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.model.Trajectory;

import java.util.function.Consumer;

/**
 * Parse a line-based input into a trajectory consisting of numerical fields.
 * @author Luke Bermingham
 */
public class TrajectoryParser extends AbstractTrajectoryParser<Trajectory> {

    /**
     * Default constructor.
     * Id is in index 0, and index 1 and 2 are numeric fields.
     */
    public TrajectoryParser() {
        this(new IdFieldResolver(0), new NumericFieldsResolver(1,2));
    }

    public TrajectoryParser(IdResolver idResolver, NumericFieldsResolver numericFieldsResolver){
        super(idResolver, numericFieldsResolver);
    }

    @Override
    protected Trajectory makeNewTrajectory() {
        return new Trajectory();
    }

    @Override
    protected void addCoordinates(Trajectory traj, double[] coords, String[] lineParts) {
        traj.add(coords);
    }

    @Override
    protected String getCommandStringParams() {
        throw new UnsupportedOperationException("The load trajectory cli command does not support loading purely numerical trajectories yet.");
    }

    @Override
    public TrajectoryParser setNumericFieldsResolver(NumericFieldsResolver numericFieldsResolver) {
        super.setNumericFieldsResolver(numericFieldsResolver);
        return this;
    }

    @Override
    public TrajectoryParser setIdResolver(IdResolver idResolver) {
        super.setIdResolver(idResolver);
        return this;
    }

    @Override
    public TrajectoryParser setDelimiter(String delimiter) {
        super.setDelimiter(delimiter);
        return this;
    }

    @Override
    public TrajectoryParser setLineTerminators(char[][] lineTerminators) {
        super.setLineTerminators(lineTerminators);
        return this;
    }

    @Override
    public TrajectoryParser setnLinesToSkip(int nLinesToSkip) {
        super.setnLinesToSkip(nLinesToSkip);
        return this;
    }

    @Override
    public TrajectoryParser setProgressListener(Consumer<Double> progressListener) {
        super.setProgressListener(progressListener);
        return this;
    }
}
