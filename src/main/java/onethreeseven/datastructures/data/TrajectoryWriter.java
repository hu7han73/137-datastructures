package onethreeseven.datastructures.data;

import onethreeseven.common.data.AbstractWriter;
import onethreeseven.datastructures.model.ITrajectory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * A file writer for a maps of {@link ITrajectory}.
 *
 * Write a set of trajectories to file.
 * The writing strategy uses a redundant id, so output will be like
 * id, x, y
 * 1, 45, 137
 * 1, 47, 138
 * 1, 49, 139
 * 2, 35, 121
 * 2, 36, 122
 * 2, 39, 123
 * @author Luke Bermingham
 */

public class TrajectoryWriter extends AbstractWriter<Map<String, ? extends ITrajectory>> {

    @Override
    protected void write(BufferedWriter bw, Map<String, ? extends ITrajectory> trajectories) throws IOException {
        for (Map.Entry<String, ? extends ITrajectory> trajEntry : trajectories.entrySet()) {
            String id = trajEntry.getKey();
            Iterator<double[]> coordIter = trajEntry.getValue().coordinateIter();
            while(coordIter.hasNext()){
                writePt(bw, id, coordIter.next());
            }
        }
    }

    private void writePt(BufferedWriter bw, String id, double[] values) throws IOException {
        bw.write(id);
        bw.write(delimiter);
        for (int i = 0; i < values.length; i++) {
            bw.write(String.valueOf(values[i]));
            if(i < values.length-1){
                bw.write(delimiter);
            }
        }
        bw.newLine();
    }

}
