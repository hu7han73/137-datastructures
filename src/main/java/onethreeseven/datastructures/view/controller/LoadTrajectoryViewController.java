package onethreeseven.datastructures.view.controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import onethreeseven.datastructures.command.LoadTrajectory;
import onethreeseven.datastructures.data.AbstractTrajectoryParser;
import onethreeseven.datastructures.data.STStopTrajectoryParser;
import onethreeseven.datastructures.data.STTrajectoryParser;
import onethreeseven.datastructures.data.SpatialTrajectoryParser;
import onethreeseven.datastructures.data.resolver.*;
import onethreeseven.geo.projection.*;
import onethreeseven.jclimod.CLIProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

/**
 * Controller fro LoadTrajectory.fxml
 * @author Luke Bermingham
 */
public class LoadTrajectoryViewController {

    @FXML
    public Button loadBtn;
    @FXML
    public Button openTrajBtn;
    @FXML
    public Label filenameLabel;
    @FXML
    public GridPane dataFieldsGrid;
    @FXML
    public Label trajSplitLabel;
    @FXML
    public Spinner<Integer> nLinesSkipSpinner;
    @FXML
    public TextField delimiterLabel;
    @FXML
    public Label warningLabel;
    @FXML
    public ChoiceBox<AbstractGeographicProjection> projectionDropDown;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public ChoiceBox<String> recentConfigChoiceBox;
    @FXML
    public Button reloadRecentBtn;

    private File trajFile = null;
    private String trajLine = null;
    private AbstractTrajectoryParser parser = null;
    private final ArrayList<ChoiceBox<AbstractStringArrayToFieldResolver>> choiceBoxes = new ArrayList<>();

    @FXML
    protected void initialize(){
        nLinesSkipSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1));
        nLinesSkipSpinner.getValueFactory().setValue(0);
        //update when nLinesSkipped changed
        nLinesSkipSpinner.valueProperty().addListener((observable, oldValue, newValue) -> updateTrajSplitLabel());
        //update when delimiter is changed
        delimiterLabel.setOnKeyTyped(event -> populateFieldEntries());
        //projections
        projectionDropDown.getItems().addAll(
                new ProjectionEquirectangular(),
                new ProjectionMercator(),
                new ProjectionModifiedSinusoidal(),
                new ProjectionPolarEquidistant(),
                new ProjectionTransverseMercator(),
                new ProjectionUPS());
        projectionDropDown.getSelectionModel().selectFirst();
        //setup recent configs
        String[] rerunAliases = new LoadTrajectory().getRerunAliases();
        recentConfigChoiceBox.getItems().addAll(rerunAliases);
        if(rerunAliases.length > 0){
            recentConfigChoiceBox.getSelectionModel().selectFirst();
            reloadRecentBtn.setDisable(false);
        }
    }

    private static final String initDirUserPrefKey = "InitTrajDir";

    public void onOpenTrajClicked(ActionEvent actionEvent) {

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());


        FileChooser fileChooser = new FileChooser();
        String initDir = prefs.get(initDirUserPrefKey, new File("").getAbsolutePath());

        File initDirFile = new File(initDir);
        if(initDirFile.exists() && initDirFile.isDirectory()){
            fileChooser.setInitialDirectory(initDirFile);
        }

        fileChooser.setTitle("Choose trajectory file");
        trajFile = fileChooser.showOpenDialog(null);

        if(trajFile != null){
            String dir = Paths.get(trajFile.toURI()).getParent().toAbsolutePath().toString();
            prefs.put(initDirUserPrefKey, dir);
            try {
                prefs.flush();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
            filenameLabel.setText(trajFile.getName());
            updateTrajSplitLabel();
        }

    }

    private String readLine(File toRead, int nLinesToSkip){
        try {
            BufferedReader br = new BufferedReader(new FileReader(toRead));
            String line = "";
            int nLinesSkipped = -1;
            while(nLinesToSkip > nLinesSkipped){
                line = br.readLine();
                nLinesSkipped++;
            }
            br.close();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void updateTrajSplitLabel(){
        if(trajFile != null){
            int linesToSkip = nLinesSkipSpinner.getValue();
            trajLine = readLine(trajFile, linesToSkip);
            trajSplitLabel.setText(trajLine);
            populateFieldEntries();
            validateFieldValues();
        }
    }

    private void populateFieldEntries(){
        if(trajLine != null){
            //clear the grid before updating
            dataFieldsGrid.getChildren().clear();
            choiceBoxes.clear();
            //split trajline
            String delimiter = delimiterLabel.getText();
            if(!delimiter.equals("")){
                String[] fields = trajLine.split(Pattern.quote(delimiter));
                for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
                    dataFieldsGrid.add(new Label(fields[fieldIdx]), 0, fieldIdx);
                    //make drop down
                    ChoiceBox<AbstractStringArrayToFieldResolver> fieldChoiceBox =
                            new ChoiceBox<>(getFieldChoices(fieldIdx));
                    choiceBoxes.add(fieldChoiceBox);
                    fieldChoiceBox.getSelectionModel().selectFirst();
                    dataFieldsGrid.add(fieldChoiceBox, 1, fieldIdx);

                    fieldChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFieldValues());

                }
            }
        }

    }

    private String[] splitLine(){
        String delimiter = delimiterLabel.getText();
        return trajLine.split(Pattern.quote(delimiter));
    }

    private TemporalFieldResolver handleTemporalResolvers(String[] fields){
        TemporalFieldResolver temporalResolver = condenseFieldResolversOfType(TemporalFieldResolver.class);
        if(temporalResolver != null){
            LocalDateTime dateTime = temporalResolver.resolve(fields);
            if(dateTime == null){
                warningLabel.setText("After combining all the temporal fields together a valid date-time could not be made.");
                for (int temporalFieldIdx : temporalResolver.getResolutionIndices()) {
                    showInvalidAt(temporalFieldIdx);
                }
                return null;
            }else{
                for (int temporalFieldIdx : temporalResolver.getResolutionIndices()) {
                    showValidAt(dateTime.toString(), temporalFieldIdx);
                }
            }
        }
        return temporalResolver;
    }

    private IdFieldResolver handleIdResolver(String[] fields){
        IdFieldResolver idResolver = condenseFieldResolversOfType(IdFieldResolver.class);
        if(idResolver != null){
            String idStr = idResolver.resolve(fields);
            for (int idFieldIdx : idResolver.getResolutionIndices()) {
                showValidAt(idStr, idFieldIdx);
            }
        }
        return idResolver;
    }

    @SuppressWarnings("unchecked")
    private boolean otherResolverValid(String[] fields,
                                       int fieldIdx,
                                       AbstractStringArrayToFieldResolver resolver,
                                       Collection<LatFieldResolver> latResolvers,
                                       Collection<LonFieldResolver> lonResolvers,
                                       Collection<StopFieldResolver> stopMoveResolvers){
        if(resolver instanceof LatFieldResolver){
            latResolvers.add((LatFieldResolver) resolver);
        }
        else if(resolver instanceof LonFieldResolver){
            lonResolvers.add((LonFieldResolver) resolver);
        }
        else if(resolver instanceof StopFieldResolver){
            stopMoveResolvers.add((StopFieldResolver) resolver);
        }


        Object resolvedValue = resolver.resolve(fields);
        if(resolvedValue != null){
            showValidAt(resolvedValue.toString(), fieldIdx);
            return true;
        }else{
            showInvalidAt(fieldIdx);
            return false;
        }
    }

    private AbstractTrajectoryParser makeParser(AbstractGeographicProjection projection,
                                                IdResolver idResolver,
                                                Collection<LatFieldResolver> latResolvers,
                                                Collection<LonFieldResolver> lonResolvers,
                                                Collection<StopFieldResolver> stopMoveResolvers,
                                                TemporalFieldResolver temporalResolver){
        if(latResolvers.size() != 1){
            warningLabel.setText("Should have one latitude field.");
            return null;
        }
        if(lonResolvers.size() != 1){
            warningLabel.setText("Should have one longitude field.");
            return null;
        }

        if(stopMoveResolvers.size() > 1){
            warningLabel.setText("Can only have one stop/move field.");
            return null;
        }
        if(idResolver == null){
            warningLabel.setText("No id field specified, each entry will be assigned to the same trajectory.");
            idResolver = new SameIdResolver("0");
        }

        if(stopMoveResolvers.size() == 1 && temporalResolver == null){
            warningLabel.setText("A trajectory with stops/moves must also have temporal fields.");
            return null;
        }

        LatFieldResolver latFieldResolver = latResolvers.iterator().next();
        LonFieldResolver lonFieldResolver = lonResolvers.iterator().next();

        //made it here so we can make a valid parser
        if(stopMoveResolvers.size() == 1){
            return new STStopTrajectoryParser(projection,
                    idResolver,
                    latFieldResolver,
                    lonFieldResolver,
                    temporalResolver,
                    stopMoveResolvers.iterator().next(),
                    true);
        }
        if(temporalResolver != null){
            return new STTrajectoryParser(projection,
                    idResolver,
                    latFieldResolver,
                    lonFieldResolver,
                    temporalResolver,
                    true);
        }

        return new SpatialTrajectoryParser(
                idResolver,
                latFieldResolver,
                lonFieldResolver,
                projection,
                true);
    }

    private void validateFieldValues(){

        AbstractGeographicProjection projection = projectionDropDown.getValue();
        IdResolver idResolver = null;
        TemporalFieldResolver temporalResolver = null;
        ArrayList<LatFieldResolver> latResolvers = new ArrayList<>();
        ArrayList<LonFieldResolver> lonResolvers = new ArrayList<>();
        ArrayList<StopFieldResolver> stopMoveResolvers = new ArrayList<>();


        String[] fields = splitLine();
        if(fields.length == 0){
            warningLabel.setText("After splitting on the delimiter there were no fields on this line of the file.");
            setLoadButtonState();
            return;
        }

        //work down the fields
        for (int fieldIdx = 0; fieldIdx < choiceBoxes.size(); fieldIdx++) {
            AbstractStringArrayToFieldResolver resolver = choiceBoxes.get(fieldIdx).getValue();
            //when using ignore field all values are valid
            if(resolver instanceof IgnoreFieldResolver){
                showValidAt("Ignored", fieldIdx);
            }
            //when temporal field we have to check all temporal fields to know if the result is valid
            else if(resolver instanceof TemporalFieldResolver && temporalResolver == null){
                temporalResolver = handleTemporalResolvers(fields);
                if(temporalResolver == null){
                    setLoadButtonState();
                    return;
                }
            }
            //handle id fields
            else if(resolver instanceof IdFieldResolver && idResolver == null){
                idResolver = handleIdResolver(fields);
            }
            //all other resolver types
            else{
                if( !(resolver instanceof TemporalFieldResolver) && !(resolver instanceof IdFieldResolver) ){
                    if(!otherResolverValid(fields, fieldIdx, resolver, latResolvers, lonResolvers, stopMoveResolvers)){
                        setLoadButtonState();
                        return;
                    }
                }
            }
        }

        parser = makeParser(projection, idResolver, latResolvers, lonResolvers, stopMoveResolvers, temporalResolver);
        if(parser != null){
            parser.setDelimiter(delimiterLabel.getText());
            parser.setnLinesToSkip(nLinesSkipSpinner.getValue());
            warningLabel.setText("");
        }
        setLoadButtonState();
    }

    private void setLoadButtonState(){
        loadBtn.setDisable(parser == null);
    }

    private void removeFromGrid(GridPane grid, int rowIdx, int colIdx){
        Iterator<Node> childIter = grid.getChildren().iterator();
        while(childIter.hasNext()){
            Node child = childIter.next();
            int childRowIdx = GridPane.getRowIndex(child);
            int childColIdx = GridPane.getColumnIndex(child);
            if(childColIdx == colIdx && childRowIdx == rowIdx){
                childIter.remove();
            }
        }
    }

    private void showValidAt(String resolvedFieldValue, int fieldIdx){
        removeFromGrid(dataFieldsGrid, fieldIdx, 2);
        dataFieldsGrid.add(new Label("Valid: " + resolvedFieldValue), 2, fieldIdx);
    }

    private void showInvalidAt(int fieldIdx){
        removeFromGrid(dataFieldsGrid, fieldIdx, 2);
        dataFieldsGrid.add(new Label("Invalid"), 2, fieldIdx);
    }


    private <T extends AbstractStringArrayToFieldResolver<?>> T condenseFieldResolversOfType(Class<T> clazz){

        ArrayList<Integer> relevantIndices = new ArrayList<>();

        for (ChoiceBox<AbstractStringArrayToFieldResolver> choiceBox : choiceBoxes) {
            AbstractStringArrayToFieldResolver resolver = choiceBox.getSelectionModel().getSelectedItem();
            if(clazz.equals(resolver.getClass())){
                relevantIndices.add(resolver.getResolutionIndices()[0]);
            }
        }

        if(relevantIndices.isEmpty()){
            return null;
        }

        int[] relevantIndicesInt = new int[relevantIndices.size()];
        for (int i = 0; i < relevantIndices.size(); i++) {
            relevantIndicesInt[i] = relevantIndices.get(i);
        }

        try {
            return clazz.getConstructor(int[].class).newInstance(relevantIndicesInt);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;

    }

    private ObservableList<AbstractStringArrayToFieldResolver> getFieldChoices(int fieldIdx){

        return FXCollections.observableArrayList(
                new IgnoreFieldResolver(fieldIdx),
                new IdFieldResolver(fieldIdx),
                new StopFieldResolver(fieldIdx),
                new TemporalFieldResolver(fieldIdx),
                new LatFieldResolver(fieldIdx),
                new LonFieldResolver(fieldIdx)
                );
    }

    @FXML
    public void onLoadTrajClicked(ActionEvent actionEvent) {
        if(trajFile != null && parser != null){

            //get the cli command from the parser we have gone to so much effort to make
            String[] commandString = parser.getCommandString(trajFile.getAbsoluteFile()).split(" ");

            Consumer<Throwable> onFail = this::showFailedDialog;

            doLoadTrajCommand(commandString, onFail);
        }
    }

    private void doLoadTrajCommand(String[] commandString, Consumer<Throwable> onFail){

        //run on a different thread

        CompletableFuture.runAsync(() -> {
            //make cli program to load the traj
            CLIProgram program = new CLIProgram();
            LoadTrajectory command = new LoadTrajectory();
            program.addCommand(command);

            //show the progress bar and update it
            //progressBar.setVisible(true);
            command.setCustomProgressListener(percentComplete -> {
                Platform.runLater(()->{
                    progressBar.setProgress(percentComplete);
                });
            });
            boolean success = program.doCommand(commandString);

            //done now, close the window
            Platform.runLater(()->{
                //close the window we are done here
                Stage stage = (Stage) loadBtn.getScene().getWindow();
                stage.close();
            });

            if(!success){
                onFail.accept(null);
            }

        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                onFail.accept(throwable);
                return null;
            }
        });


    }

    private void showFailedDialog(Throwable throwable){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error - Failed Loading Trajectory");
            alert.setContentText("Failed to load trajectory: " +
                    (throwable == null ? "" : throwable.getMessage()));
            alert.showAndWait();
        });
    }

    public void onReloadRecent(ActionEvent actionEvent) {
        String selectedRerunAlias = recentConfigChoiceBox.getSelectionModel().getSelectedItem();
        if(selectedRerunAlias != null){
            String[] commandsString = new String[]{"lt", "-rr", selectedRerunAlias};

            //make our on fail command
            Consumer<Throwable> onFail = throwable -> {
                //remove that alias if it failed
                new LoadTrajectory().removeRerunAlias(selectedRerunAlias);
                //also remove that alias from the choices of aliases in the drop down
                Platform.runLater(()->{
                    recentConfigChoiceBox.getItems().remove(selectedRerunAlias);
                });
                showFailedDialog(throwable);
            };

            doLoadTrajCommand(commandsString, onFail);
        }
    }
}
