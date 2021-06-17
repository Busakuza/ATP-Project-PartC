package View;

import javafx.event.ActionEvent;

public interface IView {
    void playerMoved();
    void mazeSolved();
    void mazeGenerated();
    void solveMaze(ActionEvent actionEvent);
    void setPlayerPosition(int row, int col);
}
