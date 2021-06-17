package ViewModel;
import Model.*;
import Model.IModel;
import Model.MovementDirection;
//import Model.Solution;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }

//    public Solution getSolution(){
//        return model.getSolution();
//    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction;
        if (reachedGoal())
            return;
        switch (keyEvent.getCode()){
            case UP : case NUMPAD8 :
                direction = MovementDirection.UP;
                break;
            case DOWN : case NUMPAD2:
                direction = MovementDirection.DOWN;
                break;
            case LEFT : case NUMPAD4:
                direction = MovementDirection.LEFT;
                break;
            case RIGHT: case NUMPAD6:
                 direction = MovementDirection.RIGHT;
                 break;
            case NUMPAD1:
                direction = MovementDirection.DOWNLEFT;
                break;
            case NUMPAD3:
                direction = MovementDirection.DOWNRIGHT;
                break;
            case NUMPAD9:
                direction = MovementDirection.UPRIGHT;
                break;
            case NUMPAD7:
                direction = MovementDirection.UPLEFT;
                break;
            default:
                // no need to move the player...
                return;
            }

        model.updatePlayerLocation(direction);
    }

    private boolean reachedGoal() {
        int goalRow = getMaze().getGoalPosition().getRowIndex();
        int goalCol = getMaze().getGoalPosition().getColumnIndex();
        if (getPlayerCol() == goalCol && getPlayerRow() == goalRow)
            return true;
        return false;
    }

    public void solveMaze(){
        model.solveMaze();
    }

    public Solution getSolution() {
        return model.getSolution();
    }

    public void saveMaze(Stage primaryStage) {
        model.saveMaze(primaryStage);
    }

    public boolean loadMaze(Stage primaryStage) {
        return model.loadMaze(primaryStage);
    }

    public void zoomEvent(KeyEvent keyEvent) {
        model.zoomEvent(keyEvent);
    }
}
