package onethreeseven.datastructures.data;

import onethreeseven.datastructures.data.resolver.*;
import onethreeseven.datastructures.model.SpatialTrajectory;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionEquirectangular;


/**
 * Parse a trajectory data-set that has geographic spatial coordinates.
 * @author Luke Bermingham
 */
public class SpatialTrajectoryParser extends AbstractTrajectoryParser<SpatialTrajectory> {

    private AbstractGeographicProjection projection;
    private boolean inCartesianMode;

    public SpatialTrajectoryParser(IdResolver idResolver,
                                   LatFieldResolver latFieldResolver,
                                   LonFieldResolver lonFieldResolver,
                                   AbstractGeographicProjection projection,
                                   boolean inCartesianMode){
        super(idResolver, new NumericFieldsResolver(latFieldResolver, lonFieldResolver));
        this.projection = projection;
        this.inCartesianMode = inCartesianMode;
    }

    @Override
    protected SpatialTrajectory makeNewTrajectory() {
        if(projection == null){
            projection = new ProjectionEquirectangular();
        }
        return new SpatialTrajectory(inCartesianMode, projection);
    }

    @Override
    protected void addCoordinates(SpatialTrajectory traj, double[] coords, String[] lineParts) {
        traj.addGeographic(coords);
    }

    @Override
    protected String getCommandStringParams() {
        return "";
    }

    public SpatialTrajectoryParser setProjection(AbstractGeographicProjection projection) {
        this.projection = projection;
        return this;
    }

    public SpatialTrajectoryParser setInCartesianMode(boolean inCartesianMode) {
        this.inCartesianMode = inCartesianMode;
        return this;
    }
}
