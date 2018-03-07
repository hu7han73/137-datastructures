package onethreeseven.datastructures.command;

import com.beust.jcommander.Parameter;
import onethreeseven.datastructures.graphics.TrajectoryGraphic;
import onethreeseven.datastructures.model.ITrajectory;
import onethreeseven.datastructures.util.DataGeneratorUtil;
import onethreeseven.jclimod.CLICommand;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
import onethreeseven.trajsuitePlugin.transaction.AddEntitiesTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Generate trajectories to be consumed.
 * @author Luke Bermingham
 */
public class GenerateTrajectories extends CLICommand {

    private int namingIdCount = 0;

    @Parameter(names = {"-pr", "--prefix"}, description = "A prefix to prepend to each trajectory id.")
    private String namingPrefix = "traj_";

    @Parameter(names = {"-nt", "--nTrajs"}, description = "How many trajectories to generate.")
    private int nTrajs = 1;

    @Parameter(names = {"-ne", "--nEntries"}, description = "Number of entries per trajectory.")
    private int nEntriesPerTraj = 1000;

    @Parameter(names = {"-ll", "--startLatLon"}, arity = 2,
            description = "The approximate start location (latitude, longitude) of any generated trajectories.")
    private List<String> latlonStart;
    private double startLat;
    private double startLon;

    @Parameter(names = {"-ts", "--timeStep"}, description = "The time-step between trajectory entries.")
    private long timeStepMillis = 1000L;

    @Parameter(names = {"-n", "--noise"}, description = "A non-zero value specifies the average GPS noise " +
            "(in metres) to simulate for each trajectory entry.")
    private double simulatedGPSNoiseMetres = 0d;

    @Parameter(names = {"-ns", "--nStops"}, description = "A non-zero value specifies the number of stop episodes each trajectory will have.")
    private int nStops = 0;

    @Parameter(names = {"-d", "--stopDur"}, description = "If there are stops specified this will be their duration.")
    private long stopDurationMillis = 120000L;

    @Parameter(names = {"-s", "--speed"}, description = "The speed of each trajectory in metres per second.")
    private double speedMetresPerSecond = 10;

    @Override
    protected void resetParametersAfterRun(Class clazz) {
        super.resetParametersAfterRun(clazz);
        nTrajs = 1;
        nEntriesPerTraj = 1000;
        timeStepMillis = 1000L;
        speedMetresPerSecond = 10;
        stopDurationMillis = 120000L;
        namingPrefix = "traj_";
    }

    @Override
    public boolean shouldStoreRerunAlias() {
        return true;
    }

    @Override
    public String generateRerunAliasBasedOnParams() {
        return nTrajs + ((nStops > 0) ? "stopping-" : "") + "trajs";
    }

    @Override
    protected String getUsage() {
        return null;
    }

    @Override
    protected boolean parametersValid() {

        //reset id count if user bothered to specify a prefix
        if(namingPrefix != null){
            namingIdCount = 0;
        }

        nStops = Math.max(nStops, 0);
        nEntriesPerTraj = Math.max(nEntriesPerTraj, 1);
        nTrajs = Math.max(nTrajs, 1);
        stopDurationMillis = Math.abs(stopDurationMillis);
        speedMetresPerSecond = Math.abs(speedMetresPerSecond);

        if(stopDurationMillis < 1){
            stopDurationMillis = 120000L;
        }

        if(latlonStart == null){
            //start at Cairns if nothing is given
            startLat = 16.9186;
            startLon = 145.7781;
        }
        //four string values passed in, but need to check if they are valid numbers
        else if(latlonStart.size() == 2){
            String latStr = latlonStart.get(0);
            String lonStr = latlonStart.get(1);
            try{
                startLat = Double.parseDouble(latStr);
            }catch (NumberFormatException ex){
                System.err.println("The latitude you passed in was not a number, you passed: " + latStr);
                return false;
            }
            try{
                startLon = Double.parseDouble(lonStr);
            }catch (NumberFormatException ex){
                System.err.println("The longitude you passed in was not a number, you passed: " + lonStr);
                return false;
            }
            if(startLat < -90 || startLat > 90 || startLon < -180 || startLon > 180){
                System.err.println("Latitude must be [-90,90] and longitude must be [-180,180].");
                return false;
            }
        }

        if(nStops > 0){
            //check for stop duration
            if(stopDurationMillis > timeStepMillis * nEntriesPerTraj){
                System.err.println("Stop duration should not exceed total trajectory duration, try lowering it.");
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean runImpl() {

        Map<String,? extends ITrajectory> result;

        //not generating stops
        if(nStops == 0){
            result = DataGeneratorUtil.generateSpatiotemporalTrajectories(
                    nTrajs,
                    nEntriesPerTraj,
                    startLat,
                    startLon,
                    simulatedGPSNoiseMetres,
                    speedMetresPerSecond,
                    timeStepMillis);
        }
        //generating stops
        else{
            result = DataGeneratorUtil.generateTrajsWithStops(
                    nTrajs,
                    nEntriesPerTraj,
                    startLat,
                    startLon,
                    simulatedGPSNoiseMetres,
                    speedMetresPerSecond,
                    timeStepMillis,
                    stopDurationMillis,
                    nStops);
        }

        //do renaming
        Map<String, ITrajectory> renamed = new HashMap<>();
        for (Map.Entry<String, ? extends ITrajectory> entry : result.entrySet()) {
            String id = namingPrefix + namingIdCount++;
            renamed.put(id, entry.getValue());
        }
        outputTrajectories(renamed);
        return true;
    }

    protected void outputTrajectories(Map<String, ? extends ITrajectory> trajs){

        String layername = generateRerunAliasBasedOnParams();

        //make add entities transaction
        AddEntitiesTransaction transaction = new AddEntitiesTransaction();
        for (Map.Entry<String, ? extends ITrajectory> entry : trajs.entrySet()) {
            transaction.add(layername, entry.getKey(), entry.getValue(), new TrajectoryGraphic(entry.getValue()));
        }

        //use service loader to find a transaction process to process adding these trajectories
        ServiceLoader<TransactionProcessor> outputConsumers = ServiceLoader.load(TransactionProcessor.class);
        for (TransactionProcessor outputConsumer : outputConsumers) {
            outputConsumer.process(transaction);
        }
    }

    @Override
    public String getCategory() {
        return "Generation";
    }

    @Override
    public String getCommandName() {
        return "gt";
    }

    @Override
    public String[] getOtherCommandNames() {
        return new String[]{"generateTrajs", "generateTrajectories"};
    }

    @Override
    public String getDescription() {
        return "Makes a user-specified number of spatio-temporal or spatio-temporal-stop trajectories.";
    }

}
