package onethreeseven.datastructures.model;

import onethreeseven.datastructures.util.BoundsUtil;
import java.util.Iterator;

/**
 * A collection of numerical coordinates that can be iterated and
 * also queried for their bounding box (minimum/maximum)
 * @author Luke Bermingham
 */
public interface BoundingCoordinates {

    Iterator<double[]> coordinateIter();

    default double[][] getBounds(){
        Iterator<double[]> coordIter = coordinateIter();
        int nDimensions = 0;
        if(coordIter != null && coordIter.hasNext()){
            nDimensions = coordIter.next().length;
        }
        return BoundsUtil.calculateBounds(coordinateIter(), nDimensions);
    }
}
