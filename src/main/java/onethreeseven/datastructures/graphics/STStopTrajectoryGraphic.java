package onethreeseven.datastructures.graphics;

import onethreeseven.common.util.Maths;
import onethreeseven.datastructures.model.STStopPt;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.trajsuitePlugin.graphics.CirclePrefab;
import onethreeseven.trajsuitePlugin.graphics.GraphicsPrefab;
import onethreeseven.trajsuitePlugin.graphics.LabelPrefab;
import onethreeseven.trajsuitePlugin.graphics.PackedVertexData;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;
import onethreeseven.trajsuitePlugin.util.BoundsUtil;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Same as {@link TrajectoryGraphic} except for every episode of stops
 * a circle is drawn around those stops.
 * @author Luke Bermingham
 */
public class STStopTrajectoryGraphic extends TrajectoryGraphic {

    public STStopTrajectoryGraphic(STStopTrajectory traj){
        super(traj);

        //when traj color changes, so does stop color
        this.fallbackColor.addListener((observable, oldValue, newValue) -> {
            Color stopColor = calculateStopColor(newValue);
            for (GraphicsPrefab additionalPrefab : additionalPrefabs) {
                additionalPrefab.color.setValue(stopColor);
            }
        });
    }

    @Override
    public PackedVertexData createVertexData(BoundingCoordinates model) {
        PackedVertexData vertexData = super.createVertexData(model);

        //create stop circles for contiguous stops
        if(model instanceof STStopTrajectory){
            initStopPrefabsFromTraj((STStopTrajectory) model);
        }

        return vertexData;
    }

    protected void initStopPrefabsFromTraj(STStopTrajectory traj){

        //this.additionalPrefabs.clear();

        Collection<GraphicsPrefab> newPrefabs = new ArrayList<>();

        boolean addingToStopBlock = false;
        ArrayList<STStopPt> stopBlock = new ArrayList<>();

        traj.toCartesian();

        Color stopColor = calculateStopColor(this.fallbackColor.get());

        for (STStopPt stStopPt : traj) {
            //ran into a stop, add it to the stop block
            if(stStopPt.isStopped()){
                stopBlock.add(stStopPt);
                addingToStopBlock = true;
            }
            //ran into a move, clear the stop block and make a prefab
            else if(!stStopPt.isStopped() && addingToStopBlock){
                if(stopBlock.size() > 1){
                    constructAndAddPrefabs(newPrefabs, stopBlock, traj.getProjection(), stopColor);
                }
                stopBlock.clear();
                addingToStopBlock = false;
            }
        }

        //if any left over
        if(stopBlock.size() > 1){
            constructAndAddPrefabs(newPrefabs, stopBlock, traj.getProjection(), stopColor);
        }

        //overwrite any existing shapes
        if(!newPrefabs.isEmpty()){
            this.additionalPrefabs.setAll(newPrefabs);
        }

    }

    protected Color calculateStopColor(Color trajColor){
        return new Color(255 - trajColor.getRed(), 255 - trajColor.getGreen(), 255 - trajColor.getBlue());
    }

    protected void constructAndAddPrefabs(Collection<GraphicsPrefab> prefabs, ArrayList<STStopPt> stopBlock, AbstractGeographicProjection proj, Color color){
        double[][] bounds = BoundsUtil.boundToOverride(2);
        for (STStopPt stStopPt : stopBlock) {
            BoundsUtil.expandBounds(bounds, stStopPt.getCoords());
        }
        //cartesian coords
        double[] centerPt = BoundsUtil.getCenter(bounds);
        //get radius
        double radius = Maths.dist(centerPt, new double[]{bounds[0][1], bounds[1][1]});
        //convert centre to geo
        double[] latlon = proj.cartesianToGeographic(centerPt);
        CirclePrefab circlePrefab = new CirclePrefab(latlon, radius);
        circlePrefab.color.setValue(color);

        prefabs.add(circlePrefab);


        //do stop label
        LocalDateTime endTime = stopBlock.get(stopBlock.size()-1).getExtra();
        LocalDateTime startTime = stopBlock.get(0).getExtra();
        long durationSecs = ChronoUnit.SECONDS.between(startTime, endTime);
        String label = "Stop [" + durationSecs + "s]";
        LabelPrefab labelPrefab = new LabelPrefab(label, latlon);
        labelPrefab.doesScale.setValue(true);
        prefabs.add(labelPrefab);

    }
}
