package onethreeseven.datastructures.command;

import com.beust.jcommander.JCommander;
import onethreeseven.jclimod.AbstractCommandsListing;
import onethreeseven.jclimod.CLICommand;

/**
 * Commands exposed by this module, note this is "provided" in module definition.
 * @author Luke Bermingham
 */
public class DatastructuresCommandsListing extends AbstractCommandsListing {
    @Override
    protected CLICommand[] createCommands(JCommander jCommander, Object... objects) {
        return new CLICommand[]{
                new LoadTrajectory()
        };
    }
}
