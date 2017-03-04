package onethreeseven.datastructures.data;

import onethreeseven.datastructures.data.resolver.IdFieldResolver;
import onethreeseven.datastructures.data.resolver.IdResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.data.resolver.TemporalFieldResolver;
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

    private AbstractGeographicProjection projection = new ProjectionMercator();
    private boolean inCartesianMode = false;
    private final TemporalFieldResolver temporalFieldResolver;


    /**
     * Makes a spatial trajectory parser where index 0 is the id, lat/lon indices are passed
     * in and we assume a Mercator projection. Coordinates are not converted to cartesian coordinates,
     * this can be triggered by the user by called {@link SpatialTrajectory#toCartesian()}.
     * @param latIdx The index of the latitude field
     * @param lonIdx The index of the longitude field
     */
    public STTrajectoryParser(int latIdx, int lonIdx, int... temporalIndices){
        this(new ProjectionMercator(),
            new IdFieldResolver(0),
            new NumericFieldsResolver(latIdx, lonIdx),
            new TemporalFieldResolver(temporalIndices),
            false);
    }

    /**
     *
     * @param projection The map projection to use if we have to convert these coordinates to cartesian
     * @param idResolver The id resolver to use
     * @param numericFieldsResolver The index to field resolver for spatial coordinates
     * @param temporalFieldResolver The index of field resolver for time stamps
     * @param inCartesianMode If true, convert the coordinates to cartesian coordinates using the projection
     */
    public STTrajectoryParser(AbstractGeographicProjection projection,
                              IdResolver idResolver,
                              NumericFieldsResolver numericFieldsResolver,
                              TemporalFieldResolver temporalFieldResolver,
                              boolean inCartesianMode) {
        super(idResolver, numericFieldsResolver);
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
