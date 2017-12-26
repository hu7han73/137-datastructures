package onethreeseven.datastructures.data;

import onethreeseven.datastructures.data.resolver.IdFieldResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.data.resolver.SameIdResolver;
import onethreeseven.datastructures.model.STPt;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.Trajectory;
import onethreeseven.datastructures.util.DataGeneratorUtil;
import onethreeseven.geo.projection.ProjectionMercator;
import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Testing loading in some mock trajectory text file data-sets.
 * @see TrajectoryParser
 * @see SpatialTrajectoryParser
 * @author Luke Bermingham
 */
public class TrajectoryDatasetParserTest {

    private static final Logger logger = Logger.getLogger(TrajectoryDatasetParserTest.class.getSimpleName());

    @Test
    public void testParseEasyDataset() throws Exception {
        File dataset = MockData.makeEasyDataset();
        Map<String, Trajectory> trajectories = new TrajectoryParser(
                new IdFieldResolver(0),
                new NumericFieldsResolver(1,2)).setnLinesToSkip(1).parse(dataset);
        dataset.deleteOnExit();

        Assert.assertTrue("Easy data-set should have a trajectory with id '1'", trajectories.containsKey("1"));

        double[][] pts = new double[][]{
                new double[]{137, 137},
                new double[]{15, 10},
                new double[]{0, 10},
                new double[]{-10, 5},
                new double[]{0, 0}
        };

        //Check points are exactly as expected
        Trajectory trajectory = trajectories.get("1");
        for (int i = 0; i < pts.length; i++) {
            double[] actual = trajectory.get(i);
            double[] expected = pts[i];
            Assert.assertArrayEquals(expected, actual, 1e-7);
        }
    }

    @Test
    public void testParseSampleTrucks() throws URISyntaxException {
        File dataset = MockData.makeTrucksDataset();

        ProjectionMercator projection = new ProjectionMercator();

        STTrajectoryParser parser = new STTrajectoryParser(4,5,2,3)
                .setIdResolver(new IdFieldResolver(0))
                .setnLinesToSkip(1)
                .setDelimiter(";")
                .setProjection(projection)
                .setInCartesianMode(true);



        Map<String, STTrajectory> trajectories = parser.parse(dataset);

        dataset.deleteOnExit();

        //0862;1;10/09/2002;09:15:59;23.845089;38.018470;486253.80;4207588.10
        STTrajectory traj = trajectories.get("0862");
        Assert.assertTrue(traj != null);

        //make the pts, same order as they are loaded t,lat,lon
        double[][] expectedCoords = new double[][]{
                projection.geographicToCartesian(23.845089, 38.018470),
                projection.geographicToCartesian(23.845179, 38.018069),
                projection.geographicToCartesian(23.8455300, 38.018241),
        };

        LocalDateTime[] expectedTimes = new LocalDateTime[]{
                LocalDateTime.of(2002, 9, 10, 9, 15, 59),
                LocalDateTime.of(2002, 9, 10, 9, 16, 29),
                LocalDateTime.of(2002, 9, 10, 9, 17, 30)
        };

        compareActualExpectedSTTrajectory(traj, expectedCoords, expectedTimes);
    }

    @Test
    public void testParseSampleGeolife() throws URISyntaxException {
        File dataset = MockData.makeGeolifeDataset();
        ProjectionMercator projection = new ProjectionMercator();

        STTrajectoryParser parser = new STTrajectoryParser(0,1,5,6)
                .setIdResolver(new IdFieldResolver(2))
                .setProjection(projection)
                .setInCartesianMode(true);

        Map<String, STTrajectory> trajectories = parser.parse(dataset);

        dataset.deleteOnExit();

        Assert.assertTrue("Geolife data-set should have a trajectory with id '0'", trajectories.containsKey("0"));

        //make the pts, same order as they are loaded t,lat,lon
        double[][] pts = new double[][]{
                projection.geographicToCartesian(39.9764666666667, 116.330066666667),
                projection.geographicToCartesian(39.9764, 116.33015)
        };

        LocalDateTime[] times = new LocalDateTime[]{
                LocalDateTime.of(2007, 4, 12, 10, 21, 16),
                LocalDateTime.of(2007, 4, 12, 10, 21, 22)
        };

        //Check points are exactly as expected
        STTrajectory trajectory = trajectories.get("0");
        compareActualExpectedSTTrajectory(trajectory, pts, times);
    }

    private void compareActualExpectedSTTrajectory(STTrajectory trajectory,
                                                   double[][] expectedCoords,
                                                   LocalDateTime[] expectedTimes){

        for (int i = 0; i < expectedCoords.length; i++) {
            double[] actualXY = trajectory.get(i).getCoords();
            double[] expectedXY = expectedCoords[i];
            LocalDateTime actualTime = trajectory.get(i).getTime();
            LocalDateTime expectedTime = expectedTimes[i];
            Assert.assertArrayEquals(expectedXY, actualXY, 1e-7);
            Assert.assertTrue(ChronoUnit.SECONDS.between(actualTime, expectedTime) == 0);
        }
    }

    @Test
    public void testParseSpaceSeparated() throws URISyntaxException {
        File dataset = MockData.makeSpacesDataset();

        final String id = "137";

        TrajectoryParser parser = new TrajectoryParser()
                .setIdResolver(new SameIdResolver(id))
                .setNumericFieldsResolver(new NumericFieldsResolver(0))
                .setDelimiter("")
                .setLineTerminators(new char[][]{new char[]{' '}}); //set line terminator as space

        //parse trajectories
        Map<String, Trajectory> trajectories = parser.parse(dataset);

        dataset.deleteOnExit();

        Assert.assertTrue(trajectories.containsKey(id));

        Trajectory trajectory = trajectories.get(id);
        //there is 60 entries in this "spaces" dataset
        Assert.assertTrue(trajectory.size() == 60);

        //check they are equal to 2
        for (double[] coords : trajectory) {
            for (double element : coords) {
                Assert.assertTrue(element - 2 <= 1e-07);
            }
        }

    }

    @Test
    public void testGenerateWriteParse(){

        final int nTrajs = 3;
        final boolean inCartesianMode = false;
        //
        // 1 - GENERATE
        //
        Map<String, STTrajectory> expected = DataGeneratorUtil.generateSpatiotemporalTrajectories(nTrajs);
        logger.info("Generated " + nTrajs + " spatio-temporal trajectories.");
        //
        // 2 - Write
        //
        String delimiter = ",";
        SpatiotemporalTrajectoryWriter writer = new SpatiotemporalTrajectoryWriter();
        writer.setDelimiter(delimiter);
        File f = new File("testGeneratedTraj");
        if(f.exists() && f.delete()){
            logger.info("Output file already existed, so deleted old one before running test.");
        }

        writer.write(f, expected);
        logger.info("Finished writing the trajectory to file at: " + f.getAbsolutePath());
        //
        // 3 - Parse
        //
        STTrajectoryParser parser = new STTrajectoryParser(1,2,3)
                .setDelimiter(delimiter)
                .setInCartesianMode(inCartesianMode)
                .setIdResolver(new IdFieldResolver(0));

        Map<String, STTrajectory> actual = parser.parse(f);
        logger.info("Finished parsing the trajectory file.");
        //
        // 4 - Compare
        //
        for (String id : expected.keySet()) {
            Assert.assertTrue(actual.containsKey(id));
            STTrajectory expectedTraj = expected.get(id);
            STTrajectory actualTraj = actual.get(id);
            assertTrajectoryEquality(actualTraj, expectedTraj);
        }
        //
        // 5 - Cleanup
        //
        if(f.delete()){
            logger.info("Deleted file: " + f.getAbsolutePath());
        }
        else{
            logger.info("Could not delete file: " + f.getAbsolutePath());
        }
    }


    @Test
    public void testGenerateWriteAndThenIterateAndParse(){

        final int nTrajs = 10;
        final boolean inCartesianMode = false;
        //
        // 1 - GENERATE
        //
        Map<String, STTrajectory> expected = DataGeneratorUtil.generateSpatiotemporalTrajectories(nTrajs);
        logger.info("Generated " + nTrajs + " spatio-temporal trajectories.");
        //
        // 2 - Write
        //
        String delimiter = ",";
        SpatiotemporalTrajectoryWriter writer = new SpatiotemporalTrajectoryWriter();
        writer.setDelimiter(delimiter);
        File f = new File("testGeneratedTraj");
        if(f.exists() && f.delete()){
            logger.info("Output file already existed, so deleted old one before running test.");
        }

        writer.write(f, expected);
        logger.info("Finished writing the trajectory to file at: " + f.getAbsolutePath());
        //
        // 3 - Parse and Iterate
        //
        STTrajectoryParser parser = new STTrajectoryParser(1,2,3)
                .setDelimiter(delimiter)
                .setInCartesianMode(inCartesianMode)
                .setIdResolver(new IdFieldResolver(0));

        Iterator<Map.Entry<String, STTrajectory>> iter = parser.iterator(f);

        logger.info("Beginning parsing, iterating, and comparing the trajectory file.");

        //
        // 4 - Compare
        //

        int nIterations = 0;

        while(iter.hasNext()){
            Map.Entry<String, STTrajectory> entry = iter.next();
            nIterations++;
            Assert.assertTrue(expected.containsKey(entry.getKey()));
            STTrajectory expectedTraj = expected.get(entry.getKey());
            STTrajectory actualTraj = entry.getValue();
            assertTrajectoryEquality(actualTraj, expectedTraj);
        }

        Assert.assertTrue(nIterations == expected.size());

        //
        // 5 - Cleanup
        //
        if(f.delete()){
            logger.info("Deleted file: " + f.getAbsolutePath());
        }
        else{
            logger.info("Could not delete file: " + f.getAbsolutePath());
        }
    }


    private static void assertTrajectoryEquality(STTrajectory actualTraj, STTrajectory expectedTraj){
        Assert.assertTrue(expectedTraj.size() == actualTraj.size());
        for (int i = 0; i < actualTraj.size(); i++) {
            STPt expectPt = expectedTraj.get(i);
            STPt actualPt = actualTraj.get(i);
            Assert.assertArrayEquals(expectPt.getCoords(), actualPt.getCoords(), 1e-05);
            Assert.assertTrue(ChronoUnit.SECONDS.between(expectPt.getTime(), actualPt.getTime()) == 0);
        }
    }

}