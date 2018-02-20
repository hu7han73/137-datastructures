package onethreeseven.datastructures.view;

import onethreeseven.trajsuitePlugin.model.AbstractTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.AbstractMenuBarPopulator;
import onethreeseven.trajsuitePlugin.view.MenuSupplier;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenu;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenuItem;

/**
 * Menu supplier for this plugin.
 * @author Luke Bermingham
 */
public class DataStructuresMenuSupplier implements MenuSupplier {

    @Override
    public void supplyMenus(AbstractMenuBarPopulator populator, AbstractTrajSuiteProgram program) {

        TrajSuiteMenu fileMenu = new TrajSuiteMenu("File");
        TrajSuiteMenu loadSubMenu = new TrajSuiteMenu("Load");
        fileMenu.addChild(loadSubMenu);
        loadSubMenu.addChild(new TrajSuiteMenuItem("Trajectory", ()->{
            System.out.println("To do: implement trajectory loading view.");
        }));

        populator.addMenu(fileMenu);
    }

}
