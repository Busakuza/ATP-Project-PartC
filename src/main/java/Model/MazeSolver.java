package Model;

import Client.IClientStrategy;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.BreadthFirstSearch;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MazeSolver implements IClientStrategy {
    private Solution solution;
    private Maze maze;

    public MazeSolver(){
        solution = null;
//        this.maze = maze;
    }
    public void setSolution(Solution solution){
        this.solution = solution;
    }

    public Solution getSolution(){return solution;}

    @Override
    public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
        try {
            ObjectOutputStream toServer = new
                    ObjectOutputStream(outToServer);
            ObjectInputStream fromServer = new
                    ObjectInputStream(inFromServer);
            toServer.flush();

            toServer.writeObject(maze); //send maze to server
            toServer.flush();
            //read generated maze (compressed withMyCompressor) from server
            solution = (Solution) fromServer.readObject();
            if(solution==null){
                System.out.println("Problem Occured in Solution");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }
}
