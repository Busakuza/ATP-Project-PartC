package View;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MazeLogger {
    private static Logger logger = LogManager.getLogger();

    public static Logger getInstance(){return logger;}

}
