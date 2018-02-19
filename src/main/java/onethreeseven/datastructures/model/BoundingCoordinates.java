package onethreeseven.datastructures.model;

import onethreeseven.datastructures.util.BoundsUtil;
import onethreeseven.geo.model.LatLonBounds;
import java.util.Iterator;

/**
 * A collection of numerical coordinates that can be iterated and
 * also queried for their bounding box (minimum/maximum)
 * @author Luke Bermingham
 */
public interface BoundingCoordinates {

    Iterator<double[]> geoCoordinateIter();

    Iterator<double[]> coordinateIter();

    /**
     * @return The bounds of this object in cartesian coordinates.
     */
    default double[][] getBounds(){
        return BoundsUtil.calculateBounds(coordinateIter());
    }

    /**
     * Gets the geographic bounds of this object.
     * @return The geographic bounds, or null if this object has no notion of geography.
     */
    default LatLonBounds getLatLonBounds(){
        Iterator<double[]> geoCoordinateIter = geoCoordinateIter();
        if(geoCoordinateIter == null){
            return null;
        }
        double[][] geoBounds = BoundsUtil.calculateBounds(geoCoordinateIter);
        return new LatLonBounds(geoBounds[0][0], geoBounds[0][1], geoBounds[1][0], geoBounds[1][1]);
    }
}
