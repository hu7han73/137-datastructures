package onethreeseven.datastructures.view;

import onethreeseven.datastructures.model.ITrajectory;
import onethreeseven.datastructures.model.STStopTrajectory;
import onethreeseven.datastructures.util.TrajectoryStatistician;
import onethreeseven.trajsuitePlugin.model.WrappedEntity;
import onethreeseven.trajsuitePlugin.model.WrappedEntityLayer;
import onethreeseven.trajsuitePlugin.view.*;

import java.util.Map;

/**
 * Supply context menu for layer stack items relevant to this module
 * @author Luke Bermingham
 */
public class DatastructuresContextMenuSupplier implements EntityContextMenuSupplier {
    @Override
    public void supplyMenuForLayer(ContextMenuPopulator populator, WrappedEntityLayer layer) {

    }

    @Override
    public void supplyMenuForEntity(ContextMenuPopulator populator, WrappedEntity entity, String parentLayer) {

        if(entity.getModel() instanceof ITrajectory){
            TrajSuiteMenuItem stopmoveTrajMenu = new TrajSuiteMenuItem("Stats", ()->{

                Map<String, String> stats = TrajectoryStatistician.getStats((ITrajectory) entity.getModel());

                String title = entity.getId() + " stats";

                ViewUtil.showInformationWindow(title, stats);
            });
            populator.addMenu(stopmoveTrajMenu);
        }

    }
}
