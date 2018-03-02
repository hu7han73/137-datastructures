package onethreeseven.datastructures.model;

import onethreeseven.common.util.Maths;
import onethreeseven.geo.model.LatLonBounds;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionEquirectangular;
import java.util.Iterator;

/**
 * A trajectory that has two geographical or cartesian coordinates in index [0] and [1]
 * (i.e lat/lon or x/y) and one other coordinate of type T.
 * This class has two modes: cartesian or geographic.
 * Cartesian coordinates are generally in meters are are useful for
 * doing math and rendering whereas geographic coordinates are useful
 * for GIS use-cases.
 * <br>
 * Switching the two modes is performed by calling:
 * {@link #toCartesian()} and {@link #toGeographic()}
 * @param <T> the type of composite pt being used.
 * @author Luke Bermingham
 */
public class SpatioCompositeTrajectory<T extends CompositePt> extends CompositeTrajectory<T> {

    private boolean inCartesianMode = false;
    private final AbstractGeographicProjection projection;


    public SpatioCompositeTrajectory(boolean inCartesianMode, AbstractGeographicProjection projection){
        //lat lon
        super();
        this.inCartesianMode = inCartesianMode;
        this.projection = projection;
    }

    /**
     * Make a spatial trajectory that will convert points into cartesian coordinates
     * using a the mercator projection.
     */
    public SpatioCompositeTrajectory(){
        super();
        this.inCartesianMode = true;
        this.projection = new ProjectionEquirectangular();
    }


    /**
     * Converts geographic coordinates to cartesian coordinates
     * using whatever map projection was passed into the constructor.
     */
    public void toCartesian(){
        if(inCartesianMode){return;}
        //convert geographic pairs (lat/lon or easting/northing)
        //to cartesian x/y pairs
        for (T entry : this.entries) {
            double[] geo = entry.coords;
            double[] xy = projection.geographicToCartesian(geo[0], geo[1]);
            double[] coords = entry.coords;
            System.arraycopy(xy, 0, coords, 0, xy.length);
        }
        this.inCartesianMode = true;
        this.bounds = null;
    }


    /**
     * Converts cartesian coordinates to geographic coordinates
     * using whatever map projection was passed into the constructor.
     */
    public void toGeographic(){
        if(!inCartesianMode){return;}
        //convert cartesian pairs to geographic pairs
        for (T entry : this.entries) {
            double[] xy = entry.coords;
            double[] geo = projection.cartesianToGeographic(xy);
            double[] coords = entry.coords;
            System.arraycopy(geo, 0, coords, 0, geo.length);
        }
        this.inCartesianMode = false;
        this.bounds = null;
    }

    /**
     * Add a geographic entry to this trajectory and do the cartesian conversion if required.
     * Note: only [0] and [1] are used in the geographicEntry, so
     * if there is anymore dimensions they will be dropped.
     * @param compositePt a spatio-composite point whose coordinates we are assuming are geographic
     */
    protected void addGeographic(T compositePt){
        double lat = compositePt.coords[0];
        double lon = compositePt.coords[1];
        if(lat < -90 || lat > 90){
            throw new IllegalArgumentException("Latitude must be between -90 and 90, got passed: " + lat);
        }
        if(lon < -180 || lon > 180){
            throw new IllegalArgumentException("Longitude must be between -180 and 180, got passed: " + lon);
        }

        //check if we need to convert pt to cartesian before adding
        if(inCartesianMode){
            double[] xy = this.projection.geographicToCartesian(lat, lon);
            compositePt.setCoords(xy);
        }
        this.add(compositePt);
    }

    /**
     * Add a cartesian entry to this trajectory and do the geographic conversion if required.
     * Note: only [0] and [1] are used in the cartesianEntry, so
     * if there is anymore dimensions they will be dropped.
     * @param compositePt a spatio-composite pt which we assume to be in cartesian coordinates
     */
    protected void addCartesian(T compositePt){
        //check if we need to convert the pt to geographic before adding
        if(!inCartesianMode){
            double[] geo = this.projection.cartesianToGeographic(compositePt.coords);
            compositePt.setCoords(geo);
        }
        this.add(compositePt);
    }

    public LatLonBounds calculateGeoBounds(){
        return new LatLonBounds(getGeoIter());
    }

    /**
     * @return An iterator of geographic coordinates (in their coordinate system)
     * the conversion resolve cartesian coordinates, if needed, is done automatically.
     */
    public Iterator<double[]> getGeoIter(){
        return new Iterator<double[]>() {
            final Iterator<T> stIter = entries.iterator();
            @Override
            public boolean hasNext() {
                return stIter.hasNext();
            }

            @Override
            public double[] next() {
                T compositePt = stIter.next();
                double[] coords = compositePt.coords;
                if(inCartesianMode){
                    coords = projection.cartesianToGeographic(coords);
                }
                return coords;
            }
        };
    }

    /**
     * Iterator for cartesian coordinates of this trajectory. Even if the trajectory is in geographic mode
     * this will not change the mode but do the conversion each time iterator.next() is called.
     * @return An iterator over cartesian coordinates of this trajectory.
     */
    @Override
    public Iterator<double[]> coordinateIter() {
        return new Iterator<double[]>() {
            final Iterator<T> stIter = entries.iterator();
            @Override
            public boolean hasNext() {
                return stIter.hasNext();
            }

            @Override
            public double[] next() {
                T compositePt = stIter.next();
                double[] coords = compositePt.coords;
                if(!inCartesianMode){
                    coords = projection.geographicToCartesian(coords[0], coords[1]);
                }
                return coords;
            }
        };
    }

    public double[] getCoords(int i, boolean inCartesianMode){
        double[] coords = getCoords(i);
        //we are in the same mode the user requested, just return the coordinates
        if(this.inCartesianMode && inCartesianMode || !this.inCartesianMode && !inCartesianMode){
            return coords;
        }
        //we are in the wrong mode, so do a conversion of the point before returning it
        return inCartesianMode ?
                projection.geographicToCartesian(coords[0], coords[1]) :
                projection.cartesianToGeographic(coords);
    }

    /**
     * Calculates the euclidean distance (in meters) between the coordinates at the two indices.
     * @param i the index of the first coordinates
     * @param j the index of the second coordinates
     * @return The distance.
     */
    public double getEuclideanDistance(int i, int j){
        double[] coordsI = getCoords(i, true);
        double[] coordsJ = getCoords(j, true);
        return Maths.dist(coordsI, coordsJ);
    }

    public double[] getCoords(int i){
        return this.entries.get(i).coords;
    }

    public boolean isInCartesianMode() {
        return inCartesianMode;
    }

    public AbstractGeographicProjection getProjection() {
        return projection;
    }

    @Override
    public Iterator<double[]> geoCoordinateIter() {
        return getGeoIter();
    }

}
