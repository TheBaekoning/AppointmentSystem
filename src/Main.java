import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.TimeConverter;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        primaryStage.setTitle("C195 Appointment System");
        primaryStage.setScene(new Scene(root, 600, 550));
        primaryStage.show();

        TimeConverter time = new TimeConverter();
        String current = "2020-07-12";

        System.out.println(time.isCurrentWeek(current));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
