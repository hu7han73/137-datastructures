package onethreeseven.datastructures.data;


import onethreeseven.common.data.AbstractLineBasedParser;
import onethreeseven.common.util.FileUtil;
import onethreeseven.datastructures.data.resolver.IdResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.model.ITrajectory;
import java.io.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Abstract trajectory parser that can be extended to parse different kinds
 * of trajectories.
 * @author Luke Bermingham
 */
public abstract class AbstractTrajectoryParser<T extends ITrajectory> extends AbstractLineBasedParser<Map<String,T>> {


    protected HashMap<String, T> output;

    protected String delimiter = ",";
    protected NumericFieldsResolver numericFieldsResolver;
    protected IdResolver idResolver;

    public AbstractTrajectoryParser(IdResolver idResolver, NumericFieldsResolver numericFieldsResolver){
        this.output = new HashMap<>();
        this.idResolver = idResolver;
        this.numericFieldsResolver = numericFieldsResolver;
    }

    @Override
    protected void parseLine(String line) {
        String[] lineParts = line.split(this.delimiter);
        try{
            double[] coordinates = numericFieldsResolver.resolve(lineParts);
            String id = idResolver.resolve(lineParts);
            T t = output.get(id);
            if(t == null){
                t = makeNewTrajectory();
                output.put(id, t);
            }
            addCoordinates(t, coordinates, lineParts);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Map<String, T> done() {
        Map<String, T> o = output;
        output = new HashMap<>();
        return o;
    }

    protected abstract T makeNewTrajectory();

    /**
     * Add coordinates to the trajectory.
     * If any additional processing need to be done with
     * the line parts, do that here too.
     * @param traj The trajectory to add coordinates to.
     * @param coords The coordinates to add.
     * @param lineParts The line parts, which may or may not be used further.
     */
    protected abstract void addCoordinates(T traj, double[] coords, String[] lineParts);

    /**
     * Reads a trajectory data-set from a plain text file.
     * Each line of the file must end with a normal new line terminator like "\n".
     * The custom line terminators that can be set by calling {@link AbstractTrajectoryParser#setLineTerminators(char[][])}
     * do not apply in this method. This method also assumes that the trajectories are laid out in the file with
     * all entries that belong to one trajectory being contiguously one line after another.
     * @param trajFile The file to read the trajectories from.
     * @return An iterator that gives an id and one whole trajectory.
     * Calling {@link Iterator#next()} will give the next id and the next whole trajectory in the file.
     */
    public Iterator<Map.Entry<String, T>> iterator(File trajFile){


        try {
            final BufferedReader br = new BufferedReader(new FileReader(trajFile));

            //read past the "n" lines to skip
            if(this.nLinesToSkip > 0){
                final StringBuilder sb = new StringBuilder();
                int linesSkipped = 0;
                while((this.lineTerminators == null ? br.readLine(): FileUtil.readUntil(br, this.lineTerminators, sb, false)) != null) {
                    if(this.nLinesToSkip != linesSkipped) {
                        ++linesSkipped;
                    }
                }
            }

            Iterator<String> lineIter = br.lines().iterator();

            return new Iterator<Map.Entry<String, T>>() {

                Map.Entry<String, T> cur = null;
                boolean streamOpen = true;

                @Override
                public boolean hasNext() {
                    if(streamOpen && !lineIter.hasNext()){
                        try {
                            br.close();
                            streamOpen = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return streamOpen && lineIter.hasNext();
                }

                @Override
                public Map.Entry<String, T> next() {
                    //read all lines until the id changes
                    while(lineIter.hasNext()){
                        String line = lineIter.next();
                        String[] lineParts = line.split(AbstractTrajectoryParser.this.delimiter);
                        String id = idResolver.resolve(lineParts);
                        double[] coordinates = numericFieldsResolver.resolve(lineParts);

                        //case: no trajectory established yet to add stuff to, make one
                        if(cur == null){
                            T traj = makeNewTrajectory();
                            addCoordinates(traj, coordinates, lineParts);
                            cur = new AbstractMap.SimpleEntry<>(id, traj);
                        }
                        //case: trajectory established and this line can add to it
                        else if(cur != null && cur.getKey().equals(id)){
                            addCoordinates(cur.getValue(), coordinates, lineParts);
                        }
                        //case: trajectory established but this line is for a different traj
                        else if(cur != null && !cur.getKey().equals(id)){
                            Map.Entry<String, T> output = cur;
                            T traj = makeNewTrajectory();
                            addCoordinates(traj, coordinates, lineParts);
                            cur = new AbstractMap.SimpleEntry<>(id, traj);
                            return output;
                        }
                    }

                    //if we made it here, close the file
                    try {
                        br.close();
                        streamOpen = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return cur;
                }
            };

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, T> parse(File dataset) {
        return super.parse(dataset);
    }

    /**
     * @return The string the execute this trajectory parsing command through CLI.
     */
    public String getCommandString(File trajFile){
        StringBuilder sb = new StringBuilder();
        sb.append("lt ");
        sb.append(" -i ");
        sb.append(trajFile.getAbsolutePath());
        sb.append(idResolver.getCommandParamString());
        sb.append(numericFieldsResolver.getCommandParamString());
        sb.append(getCommandStringParams());
        sb.append(" -n ");
        sb.append(String.valueOf(nLinesToSkip));
        sb.append(" -d ");
        sb.append(delimiter);
        return sb.toString();
    }

    /**
     *
     * @return The unique CLI params of the implementing parser.
     */
    protected abstract String getCommandStringParams();

    public AbstractTrajectoryParser<T> setNumericFieldsResolver(NumericFieldsResolver numericFieldsResolver) {
        this.numericFieldsResolver = numericFieldsResolver;
        return this;
    }

    public AbstractTrajectoryParser<T> setIdResolver(IdResolver idResolver) {
        this.idResolver = idResolver;
        return this;
    }

    public AbstractTrajectoryParser<T> setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    public AbstractTrajectoryParser<T> setLineTerminators(char[][] lineTerminators) {
        super.setLineTerminators(lineTerminators);
        return this;
    }

    @Override
    public AbstractTrajectoryParser<T> setnLinesToSkip(int nLinesToSkip) {
        super.setnLinesToSkip(nLinesToSkip);
        return this;
    }

    @Override
    public AbstractTrajectoryParser<T> setProgressListener(Consumer<Double> progressListener) {
        super.setProgressListener(progressListener);
        return this;
    }
}
