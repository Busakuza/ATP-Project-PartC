package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);//changes the field in the Model
    Maze getMaze();
    void solveMaze();
//    Solution getSolution();
    void updatePlayerLocation(MovementDirection direction);
    int getPlayerRow();
    int getPlayerCol();
    void assignObserver(Observer o);
    Solution getSolution();

    void saveMaze(Stage primaryStage);

    boolean loadMaze(Stage primaryStage);

    void zoomEvent(KeyEvent keyEvent);
}
