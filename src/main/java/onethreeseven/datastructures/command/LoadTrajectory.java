package onethreeseven.datastructures.command;

import com.beust.jcommander.Parameter;
import onethreeseven.datastructures.data.AbstractTrajectoryParser;
import onethreeseven.datastructures.data.STStopTrajectoryParser;
import onethreeseven.datastructures.data.STTrajectoryParser;
import onethreeseven.datastructures.data.SpatialTrajectoryParser;
import onethreeseven.datastructures.data.resolver.*;
import onethreeseven.datastructures.model.ITrajectory;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionEquirectangular;
import onethreeseven.jclimod.CLICommand;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * Command to load various trajectory types found in this module, so far:
 * <p>{@link onethreeseven.datastructures.model.SpatialTrajectory}</p>
 * <p>{@link onethreeseven.datastructures.model.STTrajectory}</p>
 * <p>{@link onethreeseven.datastructures.model.STStopTrajectory}</p>
 * @author Luke Bermingham
 */
public class LoadTrajectory extends CLICommand {

    @Parameter(names = {"-i", "--input"}, required = true, description = "The path to the trajectory file.")
    private String inputTrajPath;

    @Parameter(names = {"-id", "--idResolver"},
            description = "Use this to specify how you want to resolve the id fields (or lack thereof) in the trajectory file." +
                    " You have the following modes of id resolution: index-based, constant-id and incremental." +
                    " [Index-based: -id 0,1 (i.e. column 0 and 1 will be used to form an id. Note: a single index is also okay).]" +
                    " [Constant-id: -id ${customid} (i.e. $137 means all rows in the file will go into one trajectory with the id 137).]" +
                    " [Incremental: -id ++ (i.e. each row in the data-set will create a new trajectory that has an id incrementing from 0.)]",
            required = true)
    private String idResolverMode;
    private IdResolver idResolver; //instantiated in #parametersValid()

    @Parameter(names = {"-ll", "--latlonResolver"},
            description = "Specifies which column indexes hold the latitude and longitude data (respectively)." +
                          " For example: -ll 0 1 (meaning latitude in column 0 and longitude in column 1).",
            arity = 2, required = true)
    private List<String> latlonColumns;
    private NumericFieldsResolver latlonResolver; //instantiated in #parametersValid()

    /*
     * OPTIONAL command arguments
     */

    @Parameter(names = {"-t", "--temporalResolver"},
            description = "Specified which columns indices contain temporal data that we wish to use." +
                    " For example: -t 0 1 2 (meaning column 0,1,2 are combined together into one timestamp with a space between each part)." +
                    " [Note: The combined date-string will be formulated in order of the indices specified, so -t 5 4 1 will combined column index 5 then 4, then 1, altogether.]" +
                    " [Also note: this software supports several date formats, but a custom format can be specified using -df.]",
            variableArity = true)
    private List<String> temporalColumns;
    private TemporalFieldResolver temporalFieldResolver; //instantiated in parametersValid()

    @Parameter(names = {"-df", "--dateFormat"},
            description = "Assuming -t specified one or more indexes, this parameter can be used to specify a date formats " +
                    "to parse the date-string produced by combining the specified temporal columns." +
                    "For example, to parse 27/01/2017 13:45:37 you could specify: -df dd/MM/yyyy HH:mm:ss" +
                    " [Note: A full list of valid date format symbols can be found at https://docs.oracle.com/javase/tutorial/i18n/format/simpleDateFormat.html ]")
    private String dateFormat;

    @Parameter(names = {"-s", "--stopResolver"},
            description = "Specifies a single column index for a column that contains the word \"STOP\"." +
                    " Any other information in that columns is converted to \"MOVE\"." +
                    " For example, -s 3 would specified the stops in column index 3 of the file.")
    private int stopIndex = -1;

    @Parameter(names = {"-n", "--nLineSkip"},
            description = "Specifies the number of lines to skip at the start of the trajectory file.")
    private int skipNLines = 1;

    @Parameter(names = {"-d", "--delimiter"},
            description = "Specifies the delimiter used to break up each line of the trajectory file into fields. By default this is a comma.")
    private String delimiter = ",";

    @Override
    protected void resetParametersAfterRun(Class clazz) {
        super.resetParametersAfterRun(clazz);
        //reset to default values
        stopIndex = -1;
        skipNLines = 1;
        delimiter = ",";
    }

    @Override
    protected String getUsage() {
        return "\na spatial trajectory: lt -id 0 -ll 1 2 \n" +
               "a spatio-temporal trajectory: lt -id 0 -ll 1 2 -t 3 4 \n" +
               "a spatio-temporal trajectory with stops: lt -id 0, -ll 1 2 -t 3 4 -s 5";
    }

    @Override
    protected boolean parametersValid() {
        if(inputTrajPath == null || inputTrajPath.isEmpty()){
            System.err.println("Cannot load a trajectory if the input path is null.");
            return false;
        }
        if(!new File(inputTrajPath).exists()){
            System.err.println("Could not find any file at: " + inputTrajPath);
            return false;
        }
        if(idResolverMode == null || idResolverMode.isEmpty()){
            System.err.println("Cannot load a trajectory because you have not specified an id resolution mode, i.e. -id 0 or -id $123.");
            return false;
        }
        //check for valid id resolver
        if(idResolverMode.equals("++")){
            idResolver = new IncrementalIdResolver();
        }
        else if(idResolverMode.startsWith("$")){
            int splitIdx = idResolverMode.indexOf("$") + 1;
            if(splitIdx == idResolverMode.length()){
                idResolver = new SameIdResolver();
            }else{
                idResolver = new SameIdResolver(idResolverMode.substring(splitIdx));
            }
        }
        else{
            String[] parts = idResolverMode.split(",");
            int[] indices = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                try{
                    indices[i] = Integer.parseInt(parts[i]);
                }catch (NumberFormatException e){
                    System.err.println("Cannot parse id resolver mode: " + idResolverMode + ". Try -id 0 if the id is in column index 0.");
                    return false;
                }
            }
            idResolver = new IdFieldResolver(indices);
        }

        //check for lat lon
        if(latlonColumns == null){
            System.err.println("Cannot load trajectory without latitude/longitude indices.");
            return false;
        }
        if(latlonColumns.size() != 2){
            System.err.println("You must specify exactly two latitude/longitude indices, you specified "
                    + latlonColumns.size() + ". Try -ll 0 1 if latitude is in column index 0 and longitude is in column index 1.");
            return false;
        }
        //try to convert string columns for lat/lon to two integers
        try{
            int latIndex = Integer.parseInt(latlonColumns.get(0));
            int lonIndex = Integer.parseInt(latlonColumns.get(1));
            latlonResolver = new NumericFieldsResolver(latIndex, lonIndex);
        }catch (NumberFormatException e){
            System.err.println("Could not parse lat/lon column indices. Make sure there is two numbers separated by spaces. " +
                    "Try -ll 0 1 if latitude is in column index 0 and longitude is in column index 1.");
            return false;
        }

        //check for temporal columns
        if(temporalColumns != null && temporalColumns.size() > 0){
            try{
                int[] temporalIndexes = new int[temporalColumns.size()];
                for (int i = 0; i < temporalColumns.size(); i++) {
                    temporalIndexes[i] = Integer.parseInt(temporalColumns.get(i));
                }

                //make temporal field resolver
                temporalFieldResolver = (dateFormat != null && !dateFormat.isEmpty()) ?
                        new TemporalFieldResolver(new Function<>() {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat, Locale.ENGLISH);
                            @Override
                            public LocalDateTime apply(String s) {
                                return LocalDateTime.parse(s, formatter);
                            }
                        }, temporalIndexes) :
                        new TemporalFieldResolver(temporalIndexes);

            }catch (NumberFormatException e){
                System.err.println("Could not parse temporal column indices. Make sure they are numbers." +
                        " If they are numbers ensure they are separated by spaces." +
                        " Try -t 0 1 2 if there is temporal data in column 0, 1, and 2.");
                return false;
            }



        }

        return true;
    }

    @Override
    protected boolean runImpl() {
        AbstractTrajectoryParser<? extends ITrajectory> parser = makeTrajectoryParser();
        parser.setnLinesToSkip(skipNLines);
        parser.setDelimiter(delimiter);
        Map<String, ? extends ITrajectory> out = parser.parse(new File(inputTrajPath));
        outputTrajectories(out);
        return true;
    }

    protected void outputTrajectories(Map<String, ? extends ITrajectory> trajs){
        ServiceLoader<ITrajectoryOutputConsumer> outputConsumers = ServiceLoader.load(ITrajectoryOutputConsumer.class);
        for (ITrajectoryOutputConsumer outputConsumer : outputConsumers) {
            outputConsumer.consume(trajs);
        }
    }

    @Override
    public String getCategory() {
        return "Input";
    }

    @Override
    public String getCommandName() {
        return "loadTrajs";
    }

    @Override
    public String[] getCommandNameAliases() {
        return new String[]{"lt", "loadTrajectories"};
    }

    @Override
    public String getDescription() {
        return "Loads trajectories into the program for further processing.";
    }

    private AbstractTrajectoryParser<? extends ITrajectory> makeTrajectoryParser(){

        final AbstractGeographicProjection projection = new ProjectionEquirectangular();
        final boolean inCartesianMode = false;

        if(temporalFieldResolver == null){
            return new SpatialTrajectoryParser(idResolver, latlonResolver, projection, inCartesianMode);
        }

        if(stopIndex == -1){

            return new STTrajectoryParser(
                    new ProjectionEquirectangular(),
                    idResolver,
                    latlonResolver,
                    temporalFieldResolver, inCartesianMode);
        } else{
            return new STStopTrajectoryParser(new ProjectionEquirectangular(),
                    idResolver,
                    latlonResolver,
                    temporalFieldResolver,
                    new StopFieldResolver(stopIndex), inCartesianMode);
        }
    }

}
