package onethreeseven.datastructures.command;

import onethreeseven.datastructures.data.MockData;
import onethreeseven.datastructures.model.*;
import onethreeseven.jclimod.CLIProgram;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.util.Map;

/**
 * Tests for the {@link LoadTrajectory} command.
 * @author Luke Bermingham
 */
public class LoadTrajectoryTest {

    private static CLIProgram program;
    private static File trucksFile;
    private static File geolifeFile;
    private static File stopsFile;
    private static File spatialOnlyFile;

    private static Map<String, ? extends ITrajectory> output;

    @BeforeClass
    public static void setup(){
        program = new CLIProgram();
        program.addCommand(new LoadTrajectory(){
            @Override
            protected void outputTrajectories(Map<String, ? extends ITrajectory> trajs) {
                output = trajs;
            }

            @Override
            public boolean shouldStoreRerunAlias() {
                return false;
            }
        });
        trucksFile = MockData.makeTrucksDataset();
        geolifeFile = MockData.makeGeolifeDataset();
        stopsFile = MockData.makeStopsDataset();
        spatialOnlyFile = MockData.makeEasyDataset();
    }

    @AfterClass
    public static void tearDown(){
        if(trucksFile.delete()){
            System.out.println("Deleted: " + trucksFile.getAbsolutePath());
        }
        if(geolifeFile.delete()){
            System.out.println("Deleted: " + geolifeFile.getAbsolutePath());
        }
    }

    @Test
    public void testBadTrajFile() {
        Assert.assertFalse(program.doCommand("lt -i fakefile.txt -id $1 -ll 0 1".split(" ")));
    }

    @Test
    public void testBadIdResolutionMode(){
        String[] args = ("lt -i " + trucksFile.getAbsolutePath() + " -id x -ll 1 2").split(" ");
        Assert.assertFalse(program.doCommand(args));
    }

    @Test
    public void testBadLatLonAsCharacters(){
        String[] args = ("lt -i " + trucksFile.getAbsolutePath() + " -id $1 -ll x y").split(" ");
        Assert.assertFalse(program.doCommand(args));
    }

    @Test
    public void testBadLatLonWithThreeValues(){
        String[] args = ("lt -i " + trucksFile.getAbsolutePath() + " -id $1 -ll 0 1 2").split(" ");
        Assert.assertFalse(program.doCommand(args));
    }

    @Test
    public void testBadLatLonWithOneValue(){
        String[] args = ("lt -i " + trucksFile.getAbsolutePath() + " -id $1 -ll 0").split(" ");
        Assert.assertFalse(program.doCommand(args));
    }

    @Test
    public void testBadTemporalIndicesValues(){
        String[] args = ("lt -i " + trucksFile.getAbsolutePath() + " -id $1 -ll 0 1 -t a b c").split(" ");
        Assert.assertFalse(program.doCommand(args));
    }

    @Test
    public void testParseTrucks(){
        String[] args = ("lt -i " + trucksFile.getAbsolutePath() + " -id 0 -ll 5 4 -t 2 3 -d ;").split(" ");
        STTrajectory expected = MockData.makeMockTrucksTrajectory();
        //load in the trajectory
        Assert.assertTrue(program.doCommand(args));

        //compare the result
        Assert.assertTrue(output.containsKey("0862"));
        ITrajectory outputTraj = output.get("0862");
        Assert.assertTrue(outputTraj instanceof STTrajectory);
        STTrajectory actual = (STTrajectory) outputTraj;
        compareCoordinates(actual, expected);
        compareTimes(actual, expected);
    }

    @Test
    public void testParseGeolife(){
        String[] args = ("lt -i " + geolifeFile.getAbsolutePath() + " -id $0 -ll 0 1 -t 5 6 -n 0").split(" ");
        STTrajectory expected = MockData.makeMockGeolifeTrajectory();
        //load in the trajectory
        Assert.assertTrue(program.doCommand(args));

        //compare the result
        Assert.assertTrue(output.containsKey("0"));
        ITrajectory outputTraj = output.get("0");
        Assert.assertTrue(outputTraj instanceof STTrajectory);
        STTrajectory actual = (STTrajectory) outputTraj;
        compareCoordinates(actual, expected);
        compareTimes(actual, expected);
    }

    @Test
    public void testParseStopsTrajectory(){
        String[] args = ("lt -i " + stopsFile.getAbsolutePath() + " -id 0 -ll 1 2 -t 3 -s 4 -n 0").split(" ");
        STStopTrajectory expected = MockData.makeMockStopsTrajectory();
        //load in the trajectory
        Assert.assertTrue(program.doCommand(args));

        //compare the result
        Assert.assertTrue(output.containsKey("1"));
        ITrajectory outputTraj = output.get("1");
        Assert.assertTrue(outputTraj instanceof STStopTrajectory);
        STStopTrajectory actual = (STStopTrajectory) outputTraj;
        compareCoordinates(actual, expected);
        compareTimes(actual, expected);
        compareStops(actual, expected);
    }

    @Test
    public void testParseSpatialTrajectory(){
        String[] args = ("lt -i " + spatialOnlyFile.getAbsolutePath() + " -id 0 -ll 1 2").split(" ");
        SpatialTrajectory expected = MockData.makeMockSpatialOnlyTrajectory();
        //load in the trajectory
        Assert.assertTrue(program.doCommand(args));

        //compare the result
        Assert.assertTrue(output.containsKey("1"));
        ITrajectory outputTraj = output.get("1");
        Assert.assertTrue(outputTraj instanceof SpatialTrajectory);
        SpatialTrajectory actual = (SpatialTrajectory) outputTraj;
        compareCoordinates(actual, expected);
    }

    private void compareStops(STStopTrajectory actual, STStopTrajectory expected){
        Assert.assertEquals(expected.size(), actual.size());

        //actual.toGeographic();
        //expected.toGeographic();

        for (int i = 0; i < expected.size(); i++) {
            STStopPt actualPt = actual.get(i);
            STStopPt expectedPt = expected.get(i);
            Assert.assertEquals(expectedPt.isStopped(), actualPt.isStopped());
        }
    }

    private void compareTimes(SpatioCompositeTrajectory<? extends STPt> actual, SpatioCompositeTrajectory<? extends STPt> expected){
        Assert.assertEquals(expected.size(), actual.size());

        //actual.toGeographic();
        //expected.toGeographic();

        for (int i = 0; i < expected.size(); i++) {
            STPt actualPt = actual.get(i);
            STPt expectedPt = expected.get(i);
            Assert.assertTrue(actualPt.getTime().equals(expectedPt.getTime()));
        }
    }

    private void compareCoordinates(SpatioCompositeTrajectory actual, SpatioCompositeTrajectory expected){
        Assert.assertEquals(expected.size(), actual.size());

        actual.toGeographic();
        expected.toGeographic();

        for (int i = 0; i < expected.size(); i++) {
            CompositePt actualPt = actual.get(i);
            CompositePt expectedPt = expected.get(i);
            Assert.assertArrayEquals(expectedPt.getCoords(), actualPt.getCoords(), 1e-05);
        }
    }

}