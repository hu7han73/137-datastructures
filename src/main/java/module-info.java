module onethreeseven.datastructures{

    requires onethreeseven.common;
    requires onethreeseven.geo;
    requires onethreeseven.jclimod;
    requires jcommander;
    requires java.logging;

    exports onethreeseven.datastructures.algorithm;
    exports onethreeseven.datastructures.data;
    exports onethreeseven.datastructures.data.resolver;
    exports onethreeseven.datastructures.model;
    exports onethreeseven.datastructures.util;
    exports onethreeseven.datastructures.command;

    uses onethreeseven.datastructures.command.ITrajectoryOutputConsumer;

    provides onethreeseven.jclimod.AbstractCommandsListing with onethreeseven.datastructures.command.DatastructuresCommandsListing;

    opens onethreeseven.datastructures.command to jcommander, onethreeseven.jclimod;

}
