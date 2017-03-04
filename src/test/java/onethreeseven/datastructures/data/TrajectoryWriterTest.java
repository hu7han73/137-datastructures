package onethreeseven.datastructures.data;


import onethreeseven.datastructures.data.resolver.IdFieldResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.model.Trajectory;
import onethreeseven.datastructures.util.DataGeneratorUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Test writing trajectories to a test file.
 * @see TrajectoryWriter
 * @author Luke Bermingham
 */
public class TrajectoryWriterTest {

    private static final String testFileName = "testTrajectories.txt";
    private static final Logger logger = Logger.getLogger(TrajectoryWriterTest.class.getSimpleName());

    @AfterClass
    public static void cleanupTest() {
        File file = new File(testFileName);
        String absolute = file.getAbsoluteFile().getAbsolutePath();
        boolean deleted = file.delete();
        if (deleted) {
            logger.info("Deleted file: " + absolute);
        } else {
            logger.info("Could not delete file: " + absolute);
        }
    }

    @Test
    public void testWriteThenParse() throws Exception {
        //generate
        Map<String, Trajectory> actual = DataGeneratorUtil.generateNumeric3dTrajectories(10);
        File trajDump = new File(testFileName);
        logger.info("Writing trajectory file to: " + trajDump.getAbsolutePath());

        //write
        new TrajectoryWriter().write(trajDump, actual);
        //read
        Map<String, Trajectory> expected = new TrajectoryParser(
                new IdFieldResolver(0),
                new NumericFieldsResolver(1,2,3)
        ).parse(trajDump);

        for (String id : expected.keySet()) {
            Trajectory actulTraj = actual.get(id);
            Trajectory expectedTraj = expected.get(id);

            Assert.assertTrue(actulTraj.size() == expectedTraj.size());

            for (int i = 0; i < actulTraj.size(); i++) {
                Assert.assertArrayEquals(expectedTraj.get(i), actulTraj.get(i), 1e-07);
            }


        }

    }
}