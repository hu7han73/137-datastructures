package onethreeseven.datastructures.data;

import onethreeseven.datastructures.data.resolver.*;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.SpatialTrajectory;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionMercator;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Read in some data and tries to parse it into
 * a map of {@link STTrajectory}.
 * @author Luke Bermingham
 */
public class STTrajectoryParser extends AbstractTrajectoryParser<STTrajectory> {

    private AbstractGeographicProjection projection;
    private boolean inCartesianMode;
    private final TemporalFieldResolver temporalFieldResolver;


    /**
     *
     * @param projection The map projection to use if we have to convert these coordinates to cartesian
     * @param idResolver The id resolver to use
     * @param latFieldResolver The index to field resolver for latitude.
     * @param lonFieldResolver The index to field resolver for longitude.
     * @param temporalFieldResolver The index of field resolver for time stamps
     * @param inCartesianMode If true, convert the coordinates to cartesian coordinates using the projection
     */
    public STTrajectoryParser(AbstractGeographicProjection projection,
                              IdResolver idResolver,
                              LatFieldResolver latFieldResolver,
                              LonFieldResolver lonFieldResolver,
                              TemporalFieldResolver temporalFieldResolver,
                              boolean inCartesianMode) {
        super(idResolver, new NumericFieldsResolver(latFieldResolver, lonFieldResolver));
        this.temporalFieldResolver = temporalFieldResolver;
        this.projection = projection;
        this.inCartesianMode = inCartesianMode;
    }

    @Override
    protected STTrajectory makeNewTrajectory() {
        return new STTrajectory(inCartesianMode, projection);
    }

    @Override
    protected void addCoordinates(STTrajectory traj, double[] coords, String[] lineParts) {
        LocalDateTime t = temporalFieldResolver.resolve(lineParts);
        traj.addGeographic(coords, t);
    }

    @Override
    protected String getCommandStringParams() {
        return temporalFieldResolver.getCommandParamString();
    }

    public STTrajectoryParser setProjection(AbstractGeographicProjection projection) {
        this.projection = projection;
        return this;
    }

    public STTrajectoryParser setInCartesianMode(boolean inCartesianMode) {
        this.inCartesianMode = inCartesianMode;
        return this;
    }

    @Override
    public STTrajectoryParser setNumericFieldsResolver(NumericFieldsResolver numericFieldsResolver) {
        super.setNumericFieldsResolver(numericFieldsResolver);
        return this;
    }

    @Override
    public STTrajectoryParser setIdResolver(IdResolver idResolver) {
        super.setIdResolver(idResolver);
        return this;
    }

    @Override
    public STTrajectoryParser setDelimiter(String delimiter) {
        super.setDelimiter(delimiter);
        return this;
    }

    @Override
    public STTrajectoryParser setLineTerminators(char[][] lineTerminators) {
        super.setLineTerminators(lineTerminators);
        return this;
    }

    @Override
    public STTrajectoryParser setnLinesToSkip(int nLinesToSkip) {
        super.setnLinesToSkip(nLinesToSkip);
        return this;
    }

    @Override
    public STTrajectoryParser setProgressListener(Consumer<Double> progressListener) {
        super.setProgressListener(progressListener);
        return this;
    }
}
