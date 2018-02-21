package onethreeseven.datastructures.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import onethreeseven.datastructures.data.resolver.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    private File trajFile = null;
    private String trajLine = null;

    @FXML
    protected void initialize(){
        nLinesSkipSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1));
        nLinesSkipSpinner.getValueFactory().setValue(0);
        //update when nLinesSkipped changed
        nLinesSkipSpinner.valueProperty().addListener((observable, oldValue, newValue) -> updateTrajSplitLabel());
        //update when delimiter is changed
        delimiterLabel.setOnKeyTyped(event -> populateFieldEntries());
    }

    public void onOpenTrajClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose trajectory file");
        trajFile = fileChooser.showOpenDialog(null);
        filenameLabel.setText(trajFile.getName());
        updateTrajSplitLabel();
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
        }
    }

    private void populateFieldEntries(){
        if(trajLine != null){
            //clear the grid before updating
            dataFieldsGrid.getChildren().clear();
            //split trajline
            String delimiter = delimiterLabel.getText();
            if(!delimiter.equals("")){
                String[] fields = trajLine.split(Pattern.quote(delimiter));
                for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
                    dataFieldsGrid.add(new Label(fields[fieldIdx]), 0, fieldIdx);
                    //make drop down
                    ChoiceBox<AbstractStringArrayToFieldResolver> fieldChoiceBox =
                            new ChoiceBox<>(getFieldChoices(fieldIdx));
                    fieldChoiceBox.getSelectionModel().selectFirst();
                    dataFieldsGrid.add(fieldChoiceBox, 1, fieldIdx);
                }
            }
        }

    }

    private ObservableList<AbstractStringArrayToFieldResolver> getFieldChoices(int fieldIdx){

        //todo: add a lat and lon and utmN and utmE resolver types

        return FXCollections.observableArrayList(
                new IdFieldResolver(fieldIdx),
                new NumericFieldsResolver(fieldIdx),
                new StopFieldResolver(fieldIdx),
                new TemporalFieldResolver(fieldIdx)
                );
    }

}
