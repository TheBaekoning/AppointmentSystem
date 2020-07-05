import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.TimeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        primaryStage.setTitle("C195 Appointment System");
        primaryStage.setScene(new Scene(root, 600, 550));
        primaryStage.show();

        String time = new TimeConverter().getLocalTime();
        String convert = new TimeConverter().convertDefaultToUtc(time);
        System.out.println(convert);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
