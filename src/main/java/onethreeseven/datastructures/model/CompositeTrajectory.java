package onethreeseven.datastructures.model;

import onethreeseven.datastructures.util.BoundsUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A numerical trajectory that has some other dimension of type T.
 * @param <T> the type of the other dimension.
 * @author Luke Bermingham
 */
class CompositeTrajectory<T extends CompositePt> implements ITrajectory<T> {

    private int nDimensions = 0;
    protected final ArrayList<T> entries;
    protected double[][] bounds;

    CompositeTrajectory(){
        this.entries = new ArrayList<>();
    }

    /**
     * @param fromIdx inclusive from index
     * @param toIdx exclusive to index
     * @return List between indices.
     */
    public List<T> get(int fromIdx, int toIdx){
        return entries.subList(fromIdx, toIdx);
    }

    @Override
    public T get(int idx) {
        return entries.get(idx);
    }

    @Override
    public void add(T entry) {
        if(nDimensions == 0){
            nDimensions = entry.coords.length;
        }
        this.entries.add(entry);
    }

    @Override
    public double[][] getBounds() {
        //return the caches bounds if calculated
        if (bounds != null) {
            return bounds;
        }
        //cache it for future
        bounds = BoundsUtil.calculateBounds(this.coordinateIter(), nDimensions);
        return bounds;
    }

    @Override
    public Iterator<double[]> coordinateIter() {

        return new Iterator<double[]>() {
            Iterator<T> iter = entries.iterator();
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public double[] next() {
                return iter.next().coords;
            }
        };
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Iterator<T> iterator() {
        return entries.iterator();
    }

}
