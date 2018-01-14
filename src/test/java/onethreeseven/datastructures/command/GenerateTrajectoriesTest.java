package onethreeseven.datastructures.command;

import onethreeseven.datastructures.model.ITrajectory;
import onethreeseven.jclimod.CLIProgram;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * Test for {@link GenerateTrajectories}
 * @author Luke Bermingham
 */
public class GenerateTrajectoriesTest {

    private static CLIProgram program;
    private static Map<String, ? extends ITrajectory> output;

    @BeforeClass
    public static void setup(){
        program = new CLIProgram();
        program.addCommand(new GenerateTrajectories(){
            @Override
            protected void outputTrajectories(Map<String, ? extends ITrajectory> trajs) {
                output = trajs;
            }
        });
    }

    @Test
    public void testGeneratingSTTrajs() {
        int nTrajs = 2;
        int numEntries = 3;
        program.doCommand(new String[]{"gt", "-nt", String.valueOf(nTrajs), "-ne", String.valueOf(numEntries)});
        Assert.assertEquals(nTrajs, output.size());
        ITrajectory traj = output.values().iterator().next();
        Assert.assertEquals(numEntries, traj.size());

        printTrajs(output);

    }

    @Test
    public void testGeneratingStopTrajsWithAllOptions() {
        int nTrajs = 2;
        int numEntries = 5;
        program.doCommand(new String[]{"gt",
                "-pr", "test_",
                "-nt", String.valueOf(nTrajs),
                "-ne", String.valueOf(numEntries),
                "-ll", "37", "137",
                "-ts", "5000",
                "-n", "10",
                "-s", "100",
                "-ns", "1",
                "-d", "10000"
                });
        Assert.assertEquals(nTrajs, output.size());
        ITrajectory traj = output.values().iterator().next();
        Assert.assertEquals(numEntries, traj.size());

        printTrajs(output);
    }

    private static void printTrajs(Map<String, ? extends ITrajectory> toPrint){
        for (Map.Entry<String, ? extends ITrajectory> entry : toPrint.entrySet()) {
            System.out.println("\nTrajectory: " + entry.getKey());
            ITrajectory trajValue = entry.getValue();
            for (int i = 0; i < trajValue.size(); i++) {
                System.out.println(trajValue.get(i));
            }
        }
    }

}