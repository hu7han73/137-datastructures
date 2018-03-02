package onethreeseven.datastructures.graphics;

import onethreeseven.datastructures.model.ITrajectory;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.trajsuitePlugin.graphics.GraphicsPayload;
import onethreeseven.trajsuitePlugin.graphics.GraphicsSupplier;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;

/**
 * Graphics supplier for this plugin.
 * @author Luke Bermingham
 */
public class DatastructuresGraphicsSuppliers implements GraphicsSupplier {

    @Override
    public <T extends BoundingCoordinates> GraphicsPayload supply(T model) {

        if(model instanceof STStopTrajectory){
            return new STStopTrajectoryGraphic((STStopTrajectory) model);
        }
        else if(model instanceof ITrajectory){
            return new TrajectoryGraphic(model);
        }


        return null;
    }

}
