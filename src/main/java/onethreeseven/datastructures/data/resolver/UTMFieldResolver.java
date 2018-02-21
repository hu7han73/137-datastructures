package onethreeseven.datastructures.data.resolver;


import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionMercator;
import onethreeseven.geo.projection.ProjectionUTM;

/**
 * Given array indices this class resolve elements resolve a string[] into a
 * a northing and easting pair, then to latitude/longitude pair, then to cartesian coordinates.
 * @author Luke Bermingham
 */
public class UTMFieldResolver extends AbstractStringArrayToFieldResolver<double[]> {

    private final ProjectionUTM utmProjection;
    private final AbstractGeographicProjection desiredProjection;

    public UTMFieldResolver(ProjectionUTM utmProjection, AbstractGeographicProjection desiredProjection, int eastingIdx, int northingIdx) {
        super(new int[]{eastingIdx, northingIdx});
        this.utmProjection = utmProjection;
        this.desiredProjection = desiredProjection;
    }

    @Override
    public double[] resolve(String[] in) {
        double easting = Double.parseDouble(in[resolutionIndices[0]].trim());
        double northing = Double.parseDouble(in[resolutionIndices[1]].trim());
        double[] latlon = utmProjection.cartesianToGeographic(new double[]{easting, northing});
        return desiredProjection.geographicToCartesian(latlon[0], latlon[1]);
    }
}
