package onethreeseven.datastructures.data;

import onethreeseven.datastructures.data.resolver.IdResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.data.resolver.StopFieldResolver;
import onethreeseven.datastructures.data.resolver.TemporalFieldResolver;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.datastructures.model.TimeAndStop;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionEquirectangular;
import java.time.LocalDateTime;

/**
 * Parser for reading in {@link STStopTrajectory}.
 * @author Luke Bermingham
 */
public class STStopTrajectoryParser extends AbstractTrajectoryParser<STStopTrajectory> {

    private boolean inCartesianMode = false;
    private AbstractGeographicProjection projection;
    private TemporalFieldResolver temporalFieldResolver;
    private StopFieldResolver stopFieldResolver;

    public STStopTrajectoryParser(IdResolver idResolver, int latIdx, int lonIdx, int stopIdx, int... timeIdx){
        this(
                new ProjectionEquirectangular(),
                idResolver,
                new NumericFieldsResolver(latIdx, lonIdx),
                new TemporalFieldResolver(timeIdx),
                new StopFieldResolver(stopIdx),
                false);
    }

    public STStopTrajectoryParser(
            AbstractGeographicProjection projection,
            IdResolver idResolver,
            NumericFieldsResolver numericFieldsResolver,
            TemporalFieldResolver temporalFieldResolver,
            StopFieldResolver stopFieldResolver,
            boolean inCartesianMode) {

        super(idResolver, numericFieldsResolver);
        this.stopFieldResolver = stopFieldResolver;
        this.temporalFieldResolver = temporalFieldResolver;
        this.inCartesianMode = inCartesianMode;
        this.projection = projection;
    }

    public STStopTrajectoryParser setProjection(AbstractGeographicProjection projection){
        this.projection = projection;
        return this;
    }

    public STStopTrajectoryParser setInCartesianMode(boolean inCartesianMode) {
        this.inCartesianMode = inCartesianMode;
        return this;
    }

    @Override
    protected STStopTrajectory makeNewTrajectory() {
        return new STStopTrajectory(inCartesianMode, projection);
    }

    @Override
    protected void addCoordinates(STStopTrajectory traj, double[] coords, String[] lineParts) {
        //coordinates 0 and 1 are lat,lon, have to derive speed
        LocalDateTime time = temporalFieldResolver.resolve(lineParts);
        boolean isStop = stopFieldResolver.resolve(lineParts);
        traj.addGeographic(coords, new TimeAndStop(time, isStop));
    }

}
