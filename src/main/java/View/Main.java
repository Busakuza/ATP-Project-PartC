package View;

import Server.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("StartUp.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze Runner");
        primaryStage.setScene(new Scene(root));
//        primaryStage.setResizable(false);
        primaryStage.show();

    }


    public static void main(String[] args) {
        //starting the maze generator & solve servers
        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeGeneratingServer.start();
        launch(args);
        mazeGeneratingServer.stop();
    }
}
