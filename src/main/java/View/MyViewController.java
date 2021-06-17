package View;

import Model.IModel;
import Model.MazeGenerator;
import Model.MyModel;
import Server.*;
import ViewModel.MyViewModel;
//import ViewModel.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements Initializable, Observer,IView {
    public MenuItem menuItemNewGame;
    public MenuItem saveMaze;
    public MenuItem loadMaze;
    public Pane pane;
    private Server solveSearchProblemServer;
    private MyViewModel viewModel;
    private Stage stage;
    private boolean BackGroundMusic;
    private MediaPlayer mediaPlayer;


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }
    private double anchorX;
    private double anchorY;

    private double offsetX;
    private double offsetY;

    public Label playerRow;
    public Label playerCol;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;

    public void setTextField_mazeRows(TextField textField_mazeRows) {
        this.textField_mazeRows = textField_mazeRows;
    }

    public void setTextField_mazeColumns(TextField textField_mazeColumns) {
        this.textField_mazeColumns = textField_mazeColumns;
    }

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int row) {
        this.updatePlayerRow.set(row+"");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int col) {
        this.updatePlayerCol.set(col+"");
    }

    public void generateMaze(ActionEvent actionEvent) {
        /**
         * NEED TO CHECK VALID INPUT IN THE PROJECT!!
         * */
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());
        if (rows<1 || cols<1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid inputs..");
            alert.show();
            return;
        }
        viewModel.generateMaze(rows,cols);
        mazeDisplayer.requestFocus();

    }

    public void setPlayerPosition(int row, int col) {
        mazeDisplayer.setPlayerPosition(row,col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
        if (mazeDisplayer.reachedGoal()) {
            mediaPlayer.stop();
            playMusic("pokemon.mp3");
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        viewModel.solveMaze();
    }
    /**
     * event handler when the key is pressed
     * */
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.CONTROL)
            mazeDisplayer.zoomEvent(keyEvent);
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();//means that the keypressed is only for the maze
    }
    /**
     * when we press the mouse on the maze the focus will be on the maze
     * */
    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
        mazeDisplayer.setImageFileNamePlayer(getClass().getResource("/images/Charmander.png").toString());
        mazeDisplayer.setImageFileNameWall(getClass().getResource("/images/wall.png").toString());
        mazeDisplayer.setImageFileNameGoal(getClass().getResource("/images/portal.png").toString());
        mazeDisplayer.requestFocus();
    }

    private void playMusic(String s) {
        BackGroundMusic = true;
//        Media song = new Media(new File(s).toURI().toString());
        mediaPlayer = new MediaPlayer(new Media(getClass().getResource("/music/"+s).toString()));
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        mediaPlayer.play();
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String)arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();
        }
    }

    public void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    public void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
        solveSearchProblemServer.stop();

    }

    public void mazeGenerated() {
        playMusic("popcorn.mp3");
        mazeDisplayer.requestFocus();
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    public void closeProgram(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void showConfigurations(ActionEvent actionEvent) {

    }

    public void newGame(ActionEvent actionEvent) {
        mediaPlayer.stop();
       Parent root=null;
       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MazeGenerate.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        menuItemNewGame = (MenuItem)actionEvent.getTarget();
        Stage stage = (Stage)menuItemNewGame.getParentPopup().getOwnerWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void saveMaze(ActionEvent actionEvent) {
//        actionEvent.
        Stage stage = (Stage)((MenuItem)actionEvent.getTarget()).getParentPopup().getOwnerWindow();
        viewModel.saveMaze(stage);
    }

    public boolean loadMaze(ActionEvent actionEvent) {
        Stage stage = (Stage)((MenuItem)actionEvent.getTarget()).getParentPopup().getOwnerWindow();
        return viewModel.loadMaze(stage);
    }

    public MazeDisplayer getMazeDisplayer() {
        return mazeDisplayer;
    }

    public void setConfigurations(ActionEvent actionEvent) {
        StartUpController startUpController = new StartUpController();
        startUpController.setConfigurations(actionEvent);
    }


    public void drag(MouseEvent mouseEvent) {
        mazeDisplayer.drag(mouseEvent);
    }

    public void releasedMouse(MouseEvent mouseEvent)
    {
        mazeDisplayer.releasedMouse(mouseEvent);
    }

    public void pressedMouse(MouseEvent mouseEvent) {
        mazeDisplayer.pressedMouse(mouseEvent);
    }

    public void Information(ActionEvent actionEvent) {
        GameInformation.info();
    }

    public void StartMusic(ActionEvent actionEvent) {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            BackGroundMusic = true;
        } else {
            BackGroundMusic = true;
            playMusic("popcorn.mp3");
        }
    }

    public void StopMusic(ActionEvent actionEvent) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            BackGroundMusic = false;
        }
    }
}

