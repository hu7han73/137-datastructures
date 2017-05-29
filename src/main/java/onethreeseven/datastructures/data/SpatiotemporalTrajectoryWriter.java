package onethreeseven.datastructures.data;

import onethreeseven.common.data.AbstractWriter;
import onethreeseven.datastructures.model.STPt;
import onethreeseven.datastructures.model.STTrajectory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Writes a map of {@link STTrajectory}.
 * Use ISO date-time for the timestamps.
 * @author Luke Bermingham
 */
public class SpatiotemporalTrajectoryWriter extends AbstractWriter<Map<String, STTrajectory>> {

    private static final DateTimeFormatter temporalFmt = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    protected boolean write(BufferedWriter bw, Map<String, STTrajectory> stTrajs) throws IOException {
        for (Map.Entry<String, STTrajectory> stEntry : stTrajs.entrySet()) {
            final String id = stEntry.getKey();
            for (STPt stPt : stEntry.getValue()) {
                bw.write(id);
                bw.write(delimiter);
                //spatial
                double[] coords = stPt.getCoords();
                for (double coord : coords) {
                    bw.write(String.valueOf(coord));
                    bw.write(delimiter);
                }
                //temporal
                String timeStamp = temporalFmt.format(stPt.getTime());
                bw.write(timeStamp);
                bw.newLine();
            }
        }
        return true;
    }
}
