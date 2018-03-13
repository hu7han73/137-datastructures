package onethreeseven.datastructures.model;

import onethreeseven.geo.projection.AbstractGeographicProjection;

/**
 * A trajectory that has two geographical or cartesian coordinates in index [0] and [1]
 * (i.e lat/lon or x/y).
 * This class has two modes: cartesian or geographic.
 * Cartesian coordinates are generally in meters are are useful for
 * doing math and rendering whereas geographic coordinates are useful
 * for GIS use-cases.
 * <br>
 * Switching the two modes is performed by calling:
 * {@link #toCartesian()} and {@link #toGeographic()}
 *
 * @author Luke Bermingham
 */
public class SpatialTrajectory extends SpatioCompositeTrajectory<SpatialTrajectory.SpatialPt> {

    public SpatialTrajectory(){
        super();
    }

    public SpatialTrajectory(boolean inCartesianMode, AbstractGeographicProjection projection){
        super(inCartesianMode, projection);
    }

    public void addCartesian(double[] coord){
        super.addCartesian(new SpatialPt(coord));
    }

    /**
     * Add a lat/lon coordinate.
     * @param geo A {lat,lon} coordinate).
     */
    public void addGeographic(double[] geo){
        super.addGeographic(new SpatialPt(geo));
    }

    public class SpatialPt extends CompositePt<Object>{
        private static final String extraStringified = "";

        SpatialPt(double[] coords) {
            super(coords);
        }

        @Override
        public Object getExtra() {
            return null;
        }

        @Override
        public String printExtra(String delimiter) {
            return extraStringified;
        }
    }

}
