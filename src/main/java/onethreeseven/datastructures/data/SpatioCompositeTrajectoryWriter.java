package onethreeseven.datastructures.data;

import onethreeseven.common.data.AbstractWriter;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Writer for spatio-composite map of trajectories
 * @author Luke Bermingham
 */
public class SpatioCompositeTrajectoryWriter extends AbstractWriter<Map<String, ? extends SpatioCompositeTrajectory>> {

    @Override
    protected boolean write(BufferedWriter bw, Map<String, ? extends SpatioCompositeTrajectory> trajMap) throws IOException {
        for (Map.Entry<String, ? extends SpatioCompositeTrajectory> entry : trajMap.entrySet()) {
            final SpatioCompositeTrajectory traj = entry.getValue();
            boolean cartesianModeTraj = traj.isInCartesianMode();
            //convert it to geographic for writing
            if(cartesianModeTraj){
                traj.toGeographic();
            }
            final int size = traj.size();
            final String id = entry.getKey();
            for (int i = 0; i < size; i++) {
                writePt(bw, id, traj.get(i));
            }
            //convert it back to cartesian
            if(cartesianModeTraj){
                traj.toCartesian();
            }
        }
        return true;
    }

    private void writePt(BufferedWriter bw, String id, CompositePt pt) throws IOException {
        bw.write(id);
        bw.write(delimiter);
        bw.write(pt.print(delimiter));
        bw.newLine();
    }

}
