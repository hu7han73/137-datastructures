package onethreeseven.datastructures.graphics;

import onethreeseven.datastructures.model.ITrajectory;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import onethreeseven.trajsuitePlugin.graphics.GraphicsPayload;
import onethreeseven.trajsuitePlugin.graphics.PackedVertexData;
import onethreeseven.trajsuitePlugin.graphics.RenderingModes;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;
import onethreeseven.trajsuitePlugin.settings.PluginSettings;
import java.util.Iterator;

/**
 * Packed verts for drawing trajectories.
 * @author Luke Bermingham
 */
public class TrajectoryGraphic extends GraphicsPayload {

    public TrajectoryGraphic(BoundingCoordinates model){
        super();
        if(model instanceof SpatioCompositeTrajectory){
            ((SpatioCompositeTrajectory) model).toCartesian();
        }
    }

    @Override
    public PackedVertexData createVertexData(BoundingCoordinates model) {

        ITrajectory traj;
        if(model instanceof ITrajectory){
            traj = (ITrajectory) model;
        }else{
            throw new IllegalArgumentException("Model must be a type of trajectory.");
        }

        if(model instanceof SpatioCompositeTrajectory){
            ((SpatioCompositeTrajectory) model).toCartesian();
        }

        PackedVertexData vertexData = new PackedVertexData(
                traj.size(), new PackedVertexData.Types[]{PackedVertexData.Types.VERTEX});

        Iterator<double[]> coordIter = traj.coordinateIter();

        while(coordIter.hasNext()){
            double[] coords = coordIter.next();
            boolean needsElevation = coords.length == 2;
            for (double coord : coords) {
                vertexData.add(coord);
            }
            if(needsElevation){
                vertexData.add(PluginSettings.smallElevation.getSetting());
            }
        }

        return vertexData;
    }

    @Override
    protected RenderingModes defaultRenderingMode() {
        return RenderingModes.LINE_STRIP;
    }

    @Override
    protected RenderingModes[] getAcceptedRenderingModes() {
        return new RenderingModes[]{
                RenderingModes.LINE_STRIP,
                RenderingModes.POINTS
        };
    }
}
