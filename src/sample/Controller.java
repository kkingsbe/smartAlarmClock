package sample;

import javafx.scene.control.Label;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Controller {
    public Label timeLabel;

    public void initialize(){
        setTheTime();
    }

    public void setTheTime (){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date;
        while (true) {
            date = new Date();
            timeLabel.setText(dateFormat.format(date));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}