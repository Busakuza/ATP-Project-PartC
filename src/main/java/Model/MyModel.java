package Model;

import Client.Client;
import Server.Configurations;
import View.MazeDisplayer;
import View.MazeLogger;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.EventHandler;
import java.io.*;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {
    private Maze maze;
//    private Solution solution;
    private int playerRow;
    private int playerCol;
    private MazeGenerator generator;
    private MazeSolver solver;
    private Stage stage;
    private Client mazeGeneratorRequest;
    private Client mazeSolveRequest;

//    private static Logger logger = LogManager.getLogger();

    public MyModel(Stage primaryStage){
        generator = new MazeGenerator();
        solver = new MazeSolver();
        this.stage = primaryStage;
    }
    @Override
    public Solution getSolution(){

        MazeLogger.getInstance().info("Sending solution");
        return solver.getSolution();
    }

    @Override
    public void saveMaze(Stage primaryStage) {
        MazeLogger.getInstance().info("User saves current maze");
        MazeLogger.getInstance().info("Maze info: rows - "+getMaze().getRow() + " cols - "+getMaze().getCol() + " start position - "+maze.getStartPosition() + " goal position - "+maze.getGoalPosition());
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MAZE files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveMazeToFile(maze, file);
        }

    }

    @Override
    public boolean loadMaze(Stage primaryStage) {
        MazeLogger.getInstance().info("Load menu opened");
        FileChooser fileChooser = new FileChooser();
        String path =getClass().getResource("/savedMaze/").getPath();
        path = path.replaceFirst("file:/","");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MAZE files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(stage);

        if (file != null)
            openFile(file);
        else
            return false;
        MazeLogger.getInstance().info("opening maze file");
        MazeLogger.getInstance().info("Maze info: rows - "+getMaze().getRow() + " cols - "+getMaze().getCol() + " start position - "+maze.getStartPosition() + " goal position - "+maze.getGoalPosition());
        setChanged();
        notifyObservers("maze generated");
        playerRow=maze.getStartPosition().getRowIndex();
        playerCol=maze.getStartPosition().getColumnIndex();
        notifyMovement();
        return true;
    }

    @Override
    public void zoomEvent(KeyEvent keyEvent) {
    }

    private void openFile(File file) {
        try{
            ObjectInputStream readFile = new ObjectInputStream(new FileInputStream(file.getPath()));
            this.maze = (Maze) readFile.readObject();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void saveMazeToFile(Maze maze, File file) {
        try {
            ObjectOutputStream mazeFileWriter =
                    new ObjectOutputStream(new FileOutputStream(file.getPath()));
            mazeFileWriter.writeObject(maze);
            mazeFileWriter.flush();
            mazeFileWriter.close();
        } catch (IOException ex) {
            MazeLogger.getInstance().error("Problem occurred during file save");
        }
    }
    public Stage getStage() {
        return stage;
    }

    /**
     * in the project we call the server Maze generator
     * we use a client that asks to generate a maze.
     * */

    @Override
    public void generateMaze(int rows, int cols) {
        MazeLogger.getInstance().info("User requests a new maze, connecting to server...");
        generator.setRow(rows);
        generator.setCol(cols);
        try{
        mazeGeneratorRequest = new Client(InetAddress.getLocalHost(), 5400, generator);}
        catch (Exception e){
            MazeLogger.getInstance().error("Problem occurred when trying to connect to server");
            MazeLogger.getInstance().error(e);
        }
        mazeGeneratorRequest.communicateWithServer();
        maze = generator.getMaze();
        MazeLogger.getInstance().info("New maze generated using " + Configurations.getInstance().getMazeGenerator() + " maze generator");
        MazeLogger.getInstance().info("Maze info: rows - "+getMaze().getRow() + " cols - "+getMaze().getCol() + " start position - "+maze.getStartPosition() + " goal position - "+maze.getGoalPosition());
        setChanged();
        notifyObservers("maze generated");
        playerRow=maze.getStartPosition().getRowIndex();
        playerCol=maze.getStartPosition().getColumnIndex();
        notifyMovement();
    }

    private void notifyMovement() {
        setChanged();
        notifyObservers("player moved");
    }

    @Override
    public Maze getMaze() {
        return maze;
    }
    /**
     * in the project we use a server for the maze solution
     * we can use the same client or a new client.
     * but we need a different server.
     *
     * */
    @Override
    public void solveMaze() {
        MazeLogger.getInstance().info("User requested a solution to the maze");
        MazeLogger.getInstance().info("Connecting to a Maze Solving Server...");
        solver.setMaze(maze);
        try{
            mazeSolveRequest = new Client(InetAddress.getLocalHost(), 5401, solver);}
        catch (Exception e){
            MazeLogger.getInstance().error("Problem occured when connecting to the Maze Solver Server");
            MazeLogger.getInstance().error(e);
        }
        MazeLogger.getInstance().info("Program will use "+ Configurations.getInstance().getMazeSolveAlgorithm()+" solving algorithm");
        mazeSolveRequest.communicateWithServer();
        setChanged();
        notifyObservers("maze solved");

    }


    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);

    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        switch(direction){
            case UP -> {
                if(playerRow>0 && maze.getMaze()[playerRow-1][playerCol]==0)
                    playerRow--;
            }
            case DOWN -> {
                if(playerRow< maze.getRow()-1 && maze.getMaze()[playerRow+1][playerCol]==0)
                    playerRow++;
            }
            case LEFT -> {
                if(playerCol> 0 &&maze.getMaze()[playerRow][playerCol-1]==0)
                    playerCol--;
            }
            case RIGHT -> {
                if (playerCol<maze.getCol()-1 && maze.getMaze()[playerRow][playerCol+1]==0)
                    playerCol++;
            }
            case UPLEFT -> {
                if (playerRow > 0 && playerCol > 0 && maze.getMaze()[playerRow - 1][playerCol - 1] == 0) {
                    playerRow--;
                    playerCol--;
                }
            }
            case UPRIGHT -> {
                if (playerRow > 0 && playerCol>0 && maze.getMaze()[playerRow - 1][playerCol + 1] == 0) {
                    playerRow--;
                    playerCol++;
                }
            }
            case DOWNLEFT -> {
                if (playerRow< maze.getRow()-1 && playerCol> 0  && maze.getMaze()[playerRow + 1][playerCol - 1] == 0) {
                    playerRow++;
                    playerCol--;
                }
            }
            case DOWNRIGHT -> {
                if (playerRow< maze.getRow()-1 && playerCol<maze.getCol()-1 && maze.getMaze()[playerRow + 1][playerCol + 1] == 0) {
                    playerRow++;
                    playerCol++;
                }
            }
        }

        notifyMovement();
    }
}
