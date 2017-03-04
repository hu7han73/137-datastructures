package onethreeseven.datastructures.data;


import onethreeseven.common.data.AbstractLineBasedParser;
import onethreeseven.datastructures.data.resolver.IdResolver;
import onethreeseven.datastructures.data.resolver.NumericFieldsResolver;
import onethreeseven.datastructures.model.ITrajectory;

import java.util.HashMap;
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
        output = null;
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
