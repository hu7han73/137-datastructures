package onethreeseven.datastructures.model;


/**
 * A composite point consisting of numerical
 * coordinate data and then some extra field T.
 * @param <T> the type of the extra field.
 * @author Luke Bermingham
 */
public abstract class CompositePt<T> {

    protected double[] coords;

    protected CompositePt(double[] coords) {
        this.coords = coords;
    }

    public double[] getCoords() {
        return coords;
    }

    public void setCoords(double[] coords) {
        this.coords = coords;
    }

    public abstract T getExtra();
    public abstract String printExtra(String delimiter);

    public String print(String delimiter){
        StringBuilder sb = new StringBuilder();
        //do coordinates
        boolean hasExtra = getExtra() != null;
        for (int i = 0; i < coords.length; i++) {
            sb.append(coords[i]);
            if(i == coords.length-1 && !hasExtra){
                break;
            }
            sb.append(delimiter);
        }
        if(hasExtra){
            sb.append(printExtra(delimiter));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return print(",");
    }



}
