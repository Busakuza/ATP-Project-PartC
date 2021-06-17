package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas{

    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty ImageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    private double anchorX;
    private double anchorY;

    public void zoomEvent(KeyEvent keyEvent) {
        Stage stage = (Stage)((Node)keyEvent.getTarget()).getScene().getWindow();

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = 1.2;
            double scale = (getScaleX());
            if (!event.isControlDown())
                return;
            if (event.getDeltaY() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }

            setScaleX(scale);
            setScaleY(scale);
            event.consume();
            //Get how much scroll was done in Y axis.

        });
        stage.addEventHandler(MouseEvent.MOUSE_PRESSED,mouseEvent -> {
            anchorX = mouseEvent.getSceneX();
            anchorY = mouseEvent.getSceneY();
        });
        stage.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseEvent -> {
            setTranslateX(mouseEvent.getSceneX() - anchorX);
            setTranslateY(mouseEvent.getSceneY() - anchorY);
        });
        stage.addEventHandler(MouseEvent.MOUSE_RELEASED,mouseEvent -> {
            setLayoutX(getTranslateX()+getLayoutX());
            setLayoutY(getTranslateY()+getLayoutY());
            setTranslateY(0);
            setTranslateX(0);
        });
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }
    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    private Maze maze;

    private int playerRow =0;
    private int playerCol=0;
    private Solution solution = null;

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int playerRow, int playerCol) {
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        if(reachedGoal()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("HOORAY! You've reached the Goal!!");
            alert.show();
        }
        draw();
    }

    public boolean reachedGoal() {
        int goalRow = maze.getGoalPosition().getRowIndex();
        int goalCol = maze.getGoalPosition().getColumnIndex();
        if (playerCol == goalCol && playerRow == goalRow) {
            setImageFileNamePlayer(getClass().getResource("/images/charmeleon.png").toString());
            draw();
            return true;
        }
        return false;
    }


    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return ImageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.ImageFileNamePlayer.set(imageFileNamePlayer);
    }
    public void drawMaze(Maze maze) {
        this.maze = maze;
        setPlayerPosition(this.maze.getStartPosition().getRowIndex(),this.maze.getStartPosition().getColumnIndex());
        draw();
    }
    private void draw(){
        int[][] map = maze.getMaze();
        if (map!= null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = map.length;
            int cols = map[0].length;

            double cellHeight = canvasHeight/rows;
            double cellWidth = canvasWidth/cols;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear canvas
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
            drawMazeGoal(graphicsContext,cellHeight,cellWidth);
            drawMazeWalls(graphicsContext,rows,cols,cellHeight,cellWidth);
            if(solution!=null)
                drawSolution(graphicsContext,cellHeight,cellWidth);
            drawMazePlayer(graphicsContext,cellHeight,cellWidth);

        }
    }

    private void drawMazeGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        Image goalImage = null;
        try{
            goalImage = new Image(getImageFileNameGoal());
        }catch (Exception e){
            MazeLogger.getInstance().error("No goal image");
        }
        double x = maze.getGoalPosition().getColumnIndex() * cellWidth;
        double y = maze.getGoalPosition().getRowIndex() * cellHeight;
        graphicsContext.setFill(Color.YELLOW);
        if (goalImage == null)
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        else
            graphicsContext.drawImage(goalImage,x,y,cellWidth,cellHeight);
    }

    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        ArrayList<AState> solutionPath = solution.getSolutionPath();
        double x,y;
        for (AState position:solutionPath) {
            MazeState state = (MazeState) position;
            x=state.getState().getColumnIndex()*cellWidth;
            y=state.getState().getRowIndex()*cellHeight;
            graphicsContext.setFill(Color.AZURE);
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        }
    }

    private void drawMazePlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        Image playerImage = null;
        try {

            playerImage = new Image(getImageFileNamePlayer());
        } catch (Exception e) {
            MazeLogger.getInstance().error("No wall image");;
        }
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        if (playerImage == null)
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        else
            graphicsContext.drawImage(playerImage,x,y,cellWidth,cellHeight);
    }

    private void drawMazeWalls(GraphicsContext graphicsContext, int rows, int cols, double cellHeight, double cellWidth) {
        Image wallImage = null;
        try {
            wallImage = new Image(getImageFileNameWall());
        } catch (Exception e) {
            MazeLogger.getInstance().error("No wall image");;
        }

        graphicsContext.setFill(Color.RED);
        int[][] map = this.maze.getMaze();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(map[i][j]==1){
                    //wall
                    double x = j*cellWidth;
                    double y = i*cellHeight;
                    if (wallImage==null)
                        graphicsContext.fillRect(x,y,cellWidth,cellHeight);
                    else
                        graphicsContext.drawImage(wallImage,x,y,cellWidth,cellHeight);
                }

            }

        }
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
    }

    public void drag(MouseEvent mouseEvent) {

//        setTranslateX(mouseEvent.getSceneX() - anchorX);
//        setTranslateY(mouseEvent.getSceneY() - anchorY);
    }

    public void releasedMouse(MouseEvent mouseEvent) {

//        setLayoutX(getTranslateX()+getLayoutX());
//        setLayoutY(getTranslateY()+getLayoutY());
//        setTranslateY(0);
//        setTranslateX(0);
    }

    public void pressedMouse(MouseEvent mouseEvent) {
//        anchorX = mouseEvent.getSceneX();
//        anchorY = mouseEvent.getSceneY();
    }


}
