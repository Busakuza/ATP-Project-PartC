package Model;

import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;

import java.io.*;

public class MazeGenerator implements IClientStrategy {
    private int row;
    private int col;
    private Maze maze;
    MazeGenerator(){}
    MazeGenerator(int row, int col){
        this.row = row;
        this.col = col;

    }
    public Maze getMaze(){return maze;}
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
        try {
            ObjectOutputStream toServer = new
                    ObjectOutputStream(outToServer);
            ObjectInputStream fromServer = new
                    ObjectInputStream(inFromServer);
            toServer.flush();
            int[] mazeDimensions = new int[]{getRow(),getCol()};
            toServer.writeObject(mazeDimensions); //send maze dimensions to server
            toServer.flush();
            byte[] compressedMaze = (byte[]) fromServer.readObject();
            //read generated maze (compressed withMyCompressor) from server
            InputStream is = new MyDecompressorInputStream(new
                    ByteArrayInputStream(compressedMaze));
            byte[] decompressedMaze = new byte[compressedMaze.length];
            //allocating byte[] for the decompressed maze -
            is.read(decompressedMaze); //Fill decompressedMaze with bytes
           this.maze = new Maze(decompressedMaze);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRow(int rows) {
        this.row = rows;
    }

    public void setCol(int cols) {
        this.col = cols;
    }
}
