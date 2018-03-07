package onethreeseven.datastructures;

import javafx.stage.Stage;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.BasicFxApplication;

/**
 * Entry point for running the commands of solely this module.
 * @author Luke Bermingham
 */
public class Main extends BasicFxApplication {


    @Override
    protected BaseTrajSuiteProgram preStart(Stage stage) {
        return BaseTrajSuiteProgram.getInstance();
    }

    @Override
    public String getTitle() {
        return "Data-structures plugin stub";
    }

    @Override
    public int getStartWidth() {
        return 640;
    }

    @Override
    public int getStartHeight() {
        return 480;
    }
}
