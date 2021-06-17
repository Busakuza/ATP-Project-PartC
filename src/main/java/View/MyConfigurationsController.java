package View;

import Server.Configurations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MyConfigurationsController implements Initializable {
    public TextField numOfThreads;
    public ComboBox<String> generatorComboBox;
    public ComboBox<String> solverComboBox;
    private String mazeGenerator;
    private String mazeSolver;

    ObservableList<String> generators =
            FXCollections.observableArrayList("EmptyMazeGenerator","SimpleMazeGenerator","MyMazeGenerator");
    ObservableList<String> solvers =
            FXCollections.observableArrayList("BreadthFirstSearch","DepthFirstSearch","BestFirstSearch");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generatorComboBox.setItems(generators);
        solverComboBox.setItems(solvers);

        numOfThreads.setPromptText(String.valueOf(Configurations.getInstance().getNumOfThreads()));
        generatorComboBox.setPromptText(Configurations.getInstance().getMazeGenerator());
        solverComboBox.setPromptText(Configurations.getInstance().getMazeSolveAlgorithm());
    }

    public void saveChanges(ActionEvent actionEvent) {
        if(!checkValidInputs())
            return;
        Configurations.getInstance().setMazeGenAlgorithm(generatorComboBox.getValue());
        Configurations.getInstance().setMazeSearchingAlgorithm(solverComboBox.getValue());
        if(numOfThreads.getText() != "")
            Configurations.getInstance().setNumOfThreads(numOfThreads.getText());

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    private boolean checkValidInputs() {
        if(generatorComboBox.getValue() == null)
            generatorComboBox.setValue(Configurations.getInstance().getMazeGenerator());
        if(solverComboBox.getValue()==null)
            solverComboBox.setValue(Configurations.getInstance().getMazeSolveAlgorithm());
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Invalid value of threads");
        try{
            if(numOfThreads.getText() == "")
                return true;
            int thread = Integer.valueOf(numOfThreads.getText());
        }
        catch (Exception e){
            alert.show();
            return false;
        }
        return true;
    }

    public void cancelChanges(ActionEvent actionEvent) {
        //close Stage
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }


    public void changedGeneratorBox(ActionEvent actionEvent) {
        generatorComboBox.setPromptText(Configurations.getInstance().getMazeGenerator());
    }

    public void changedSolverBox(ActionEvent actionEvent) {
        solverComboBox.setPromptText(Configurations.getInstance().getMazeSolveAlgorithm());
    }
}
