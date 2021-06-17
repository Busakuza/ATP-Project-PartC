package View;
import Model.*;
import Model.IModel;
import ViewModel.*;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class StartUpController {

    public MenuItem menuItemConfig;
    private Stage stage;
    private Scene scene;
    private Parent root;
//    private static Logger logger = LogManager.getLogger();
    //move to a new scene
    public void startNewGame(ActionEvent event){
        MazeLogger.getInstance().info("Starting new game");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MazeGenerate.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void closeProgram(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void loadMaze(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MyView.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        // update MVVM
        IModel model = new MyModel(stage);
        MyViewModel viewModel = new MyViewModel(model);
        // getController will return the View Class
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        if(!viewModel.loadMaze(stage))
            return;
        //show new scene
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        view.getMazeDisplayer().requestFocus();
    }

    public void setConfigurations(ActionEvent actionEvent) {
        MazeLogger.getInstance().info("Opening Maze Runner Configuration window");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MyConfigurations.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            MazeLogger.getInstance().error("Problem occurred opening configurations");
            MazeLogger.getInstance().error(e);
        }
        Stage configStage = new Stage();
        scene = new Scene(root);
        configStage.setScene(scene);
        configStage.setTitle("Configuration");
        configStage.initModality(Modality.APPLICATION_MODAL);
        configStage.show();

    }

    public void information(ActionEvent actionEvent) {
        GameInformation.info();
    }
}
