package View;
import Model.*;
import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MazeGenerateController {
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void generateMaze(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MyView.fxml"));
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Invalid inputs..");
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int rows,cols;
        //looking for valid inputs
        try {

            rows = Integer.valueOf(textField_mazeRows.getText());
            cols = Integer.valueOf(textField_mazeColumns.getText());
            if(rows<1 || cols<1)
                throw new Exception();
        }
        catch(Exception e){
            alert.show();
            return;
        }
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        //update mvvm
        IModel model = new MyModel(stage);
        MyViewModel viewModel = new MyViewModel(model);
        // getController will return the View Class
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        view.setTextField_mazeColumns(textField_mazeColumns);
        view.setTextField_mazeRows(textField_mazeRows);
        view.generateMaze(actionEvent);
    }

    public void closeProgram(ActionEvent actionEvent) {
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
