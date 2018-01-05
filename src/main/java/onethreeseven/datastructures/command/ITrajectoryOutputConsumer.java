package onethreeseven.datastructures.command;

import onethreeseven.datastructures.model.ITrajectory;

import java.util.Map;

/**
 * An interface that service loaders can implement to consume trajectories
 * from the {@link LoadTrajectory} command.
 * @author Luke Bermingham
 */
public interface ITrajectoryOutputConsumer {

    public void consume(Map<String,? extends ITrajectory> trajectory);

}
