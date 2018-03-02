module onethreeseven.datastructures{

    requires transitive onethreeseven.common;
    requires transitive onethreeseven.geo;
    requires transitive onethreeseven.jclimod;
    requires transitive onethreeseven.trajsuitePlugin;
    requires jcommander;
    requires java.logging;
    requires javafx.graphics;
    requires java.prefs;
    requires java.desktop;

    exports onethreeseven.datastructures.algorithm;
    exports onethreeseven.datastructures.data;
    exports onethreeseven.datastructures.data.resolver;
    exports onethreeseven.datastructures.model;
    exports onethreeseven.datastructures.util;
    exports onethreeseven.datastructures.command;
    exports onethreeseven.datastructures.view;

    //so we can run javafx
    exports onethreeseven.datastructures to javafx.graphics;
    exports onethreeseven.datastructures.view.controller to javafx.fxml;
    opens onethreeseven.datastructures.view.controller to javafx.fxml;

    //provide this plug-ins menu to the main program
    provides onethreeseven.trajsuitePlugin.view.MenuSupplier with onethreeseven.datastructures.view.DataStructuresMenuSupplier;

    //uses this interface to provide loaded trajectories to other modules
    uses onethreeseven.trajsuitePlugin.model.EntityConsumer;

    provides onethreeseven.jclimod.AbstractCommandsListing with onethreeseven.datastructures.command.DatastructuresCommandsListing;

    opens onethreeseven.datastructures.command to jcommander, onethreeseven.jclimod;

    //send our graphics supplier as a service
    provides onethreeseven.trajsuitePlugin.graphics.GraphicsSupplier with onethreeseven.datastructures.graphics.DatastructuresGraphicsSuppliers;

}
