package onethreeseven.datastructures.data;

import onethreeseven.datastructures.data.resolver.IdFieldResolver;
import onethreeseven.datastructures.data.resolver.IdResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.model.SpatialTrajectory;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionEquirectangular;


/**
 * Parse a trajectory data-set that has geographic spatial coordinates.
 * @author Luke Bermingham
 */
public class SpatialTrajectoryParser extends AbstractTrajectoryParser<SpatialTrajectory> {

    private AbstractGeographicProjection projection;
    private boolean inCartesianMode = false;

    /**
     * Makes a spatial trajectory parser where index 0 is the id, lat/lon indices are passed
     * in and we assume a Mercator projection. Coordinates are not converted to cartesian coordinates,
     * this can be triggered by the user by called {@link SpatialTrajectory#toCartesian()}.
     * @param latIdx The index of the latitude field
     * @param lonIdx The index of the longitude field
     */
    public SpatialTrajectoryParser(int latIdx, int lonIdx){
        this(new IdFieldResolver(0), latIdx, lonIdx);
    }

    public SpatialTrajectoryParser(IdResolver idResolver, int latIdx, int lonIdx){
        super(idResolver,new NumericFieldsResolver(latIdx, lonIdx));
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

    public SpatialTrajectoryParser setProjection(AbstractGeographicProjection projection) {
        this.projection = projection;
        return this;
    }

    public SpatialTrajectoryParser setInCartesianMode(boolean inCartesianMode) {
        this.inCartesianMode = inCartesianMode;
        return this;
    }
}
