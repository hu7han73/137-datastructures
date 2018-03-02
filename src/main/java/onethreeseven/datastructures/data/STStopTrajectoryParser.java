package onethreeseven.datastructures.data;

import onethreeseven.datastructures.data.resolver.*;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.datastructures.model.TimeAndStop;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import java.time.LocalDateTime;

/**
 * Parser for reading in {@link STStopTrajectory}.
 * @author Luke Bermingham
 */
public class STStopTrajectoryParser extends AbstractTrajectoryParser<STStopTrajectory> {

    private boolean inCartesianMode;
    private AbstractGeographicProjection projection;
    private TemporalFieldResolver temporalFieldResolver;
    private StopFieldResolver stopFieldResolver;

    public STStopTrajectoryParser(
            AbstractGeographicProjection projection,
            IdResolver idResolver,
            LatFieldResolver latFieldResolver,
            LonFieldResolver lonFieldResolver,
            TemporalFieldResolver temporalFieldResolver,
            StopFieldResolver stopFieldResolver,
            boolean inCartesianMode) {

        super(idResolver, new NumericFieldsResolver(latFieldResolver, lonFieldResolver));
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

    @Override
    protected String getCommandStringParams() {
        return temporalFieldResolver.getCommandParamString() + stopFieldResolver.getCommandParamString();
    }

}
