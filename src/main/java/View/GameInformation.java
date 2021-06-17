package View;

import javafx.scene.control.Alert;

public class GameInformation {

    public static void info(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setContentText("מטרת המשחק: לעזור לצ'רמנדר להתפתח לצ'רמיליון בעזרת פתירת המבוך.\n" +
                "\n" +
                "כללי המשחק: כדי להתקדם עם צ'רמנדבר ליעד עליך להעזר בכפתורים הבאים במקלדת:\n" +
                "\n" +
                "בעזרת מקשי המספרים:\n" +
                "2-למטה\n" +
                "4-שמאלה\n" +
                "6-ימינה\n" +
                "8-למעלה\n" +
                "התקדמות באלכסונים:\n" +
                "1-אלכסון שמאלה+למטה\n" +
                "3-אלכסון ימינה+למטה\n" +
                "7-אלכסון שמאלה+למעלה\n" +
                "9-אלכסון ימינה+למעלה\n" +
                "\n" +
                "ובנוסף\n" +
                "ניתן לזוז בעזרת מקשי החצים שבמקלדת בהתאמה \n");
        alert.show();
    }
}
