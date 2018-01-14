package onethreeseven.datastructures.data;

import onethreeseven.common.util.FileUtil;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.SpatialTrajectory;
import onethreeseven.datastructures.model.TimeAndStop;
import onethreeseven.geo.projection.ProjectionEquirectangular;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Some mock data for the tests in this package.
 * @author Luke Bermingham
 */
public class MockData {

    private static File makeMockDataset(String dataStr){
        File mockDataset = FileUtil.makeTempFile();
        try {
            FileWriter fw = new FileWriter(mockDataset);
            fw.write(dataStr);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mockDataset;
    }

    public static File makeEasyDataset(){
        return makeMockDataset(getSpatialOnlyDataString());
    }

    public static File makeGeolifeDataset(){
        return makeMockDataset(getGeolifeDataString());
    }

    public static File makeSpacesDataset(){
        return makeMockDataset(getSpacesDataString());
    }

    public static File makeTrucksDataset(){
        return makeMockDataset(getTrucksDataString());
    }

    public static File makeStopsDataset(){
        return makeMockDataset(getStopTrajectoryDataString());
    }

    public static String getSpatialOnlyDataString(){
        return "id x y\n" +
                "1,137,137\n" +
                "1,15,10\n" +
                "1,0,10\n" +
                "1,-10,5\n" +
                "1,0,0";
    }

    public static SpatialTrajectory makeMockSpatialOnlyTrajectory(){
        SpatialTrajectory spatialTrajectory = new SpatialTrajectory(false, new ProjectionEquirectangular());
        spatialTrajectory.addGeographic(new double[]{137,137});
        spatialTrajectory.addGeographic(new double[]{15,10});
        spatialTrajectory.addGeographic(new double[]{0,10});
        spatialTrajectory.addGeographic(new double[]{-10,5});
        spatialTrajectory.addGeographic(new double[]{0,0});
        return spatialTrajectory;
    }

    public static String getGeolifeDataString(){
        return "39.9764666666667,116.330066666667,0,173.884514435696,39184.4314351852,2007-04-12,10:21:16\n" +
                "39.9764,116.33015,0,173.884514435696,39184.4315046296,2007-04-12,10:21:22";
    }

    public static STTrajectory makeMockGeolifeTrajectory(){
        STTrajectory traj = new STTrajectory(false, new ProjectionEquirectangular());
        traj.addGeographic(new double[]{39.9764666666667,116.330066666667}, LocalDateTime.of(2007, 4, 12, 10, 21, 16));
        traj.addGeographic(new double[]{39.9764,116.33015}, LocalDateTime.of(2007, 4, 12, 10, 21, 22));
        return traj;
    }

    public static String getSpacesDataString(){
        return "2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2";
    }

    public static String getTrucksDataString(){
        return "id disregard date  time lon       lat    utm e     utm n\n" +
                "0862;1;10/09/2002;09:15:59;23.845089;38.018470;486253.80;4207588.10\n" +
                "0862;1;10/09/2002;09:16:29;23.845179;38.018069;486261.60;4207543.60\n" +
                "0862;1;10/09/2002;09:17:30;23.845530;38.018241;486292.40;4207562.60";
    }

    public static STTrajectory makeMockTrucksTrajectory(){
        STTrajectory traj = new STTrajectory(false, new ProjectionEquirectangular());
        traj.addGeographic(new double[]{38.018470, 23.845089}, LocalDateTime.of(2002, 9, 10, 9, 15, 59));
        traj.addGeographic(new double[]{38.018069, 23.845179}, LocalDateTime.of(2002, 9, 10, 9, 16, 29));
        traj.addGeographic(new double[]{38.018241, 23.845530}, LocalDateTime.of(2002, 9, 10, 9, 17, 30));
        return traj;
    }

    public static String getStopTrajectoryDataString(){
        return "1, -27.465790999999992, 153.03361699999996, 2016-11-22T08:08:53, MOVING\n" +
                "1, -27.465794999999996, 153.033599, 2016-11-22T08:08:54, MOVING\n" +
                "1, -27.465800000000005, 153.0336, 2016-11-22T08:08:55, STOPPED\n" +
                "1, -27.465802, 153.033593, 2016-11-22T08:08:56, STOPPED";
    }

    public static STStopTrajectory makeMockStopsTrajectory(){
        STStopTrajectory traj = new STStopTrajectory(false, new ProjectionEquirectangular());
        final boolean STOPPED = true;
        final boolean MOVING = false;
        traj.addGeographic(new double[]{-27.465790999999992, 153.03361699999996},
                new TimeAndStop(LocalDateTime.of(2016, 11, 22, 8, 8, 53), MOVING));

        traj.addGeographic(new double[]{-27.465794999999996, 153.033599},
                new TimeAndStop(LocalDateTime.of(2016, 11, 22, 8, 8, 54), MOVING));

        traj.addGeographic(new double[]{-27.465800000000005, 153.0336},
                new TimeAndStop(LocalDateTime.of(2016, 11, 22, 8, 8, 55), STOPPED));

        traj.addGeographic(new double[]{-27.465802, 153.033593},
                new TimeAndStop(LocalDateTime.of(2016, 11, 22, 8, 8, 56), STOPPED));
        return traj;
    }

}
