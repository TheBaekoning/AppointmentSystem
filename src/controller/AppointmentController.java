package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class AppointmentController {
    private static User userData;
    @FXML
    Button addButton;
    @FXML
    Button updateButton;
    @FXML
    Button customerButton;

    public void addAppointment() throws IOException {
        System.out.println(userData.getUsername());
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/addAppointment.fxml"));
        stage = (Stage) addButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        AddAppointment addAppointment = loader.getController();
    }

    public void updateAppointment() throws IOException {
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/updateAppointment.fxml"));
        stage = (Stage) updateButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        UpdateAppointment updateAppointment = loader.getController();

    }

    public void customerButtonClicked() throws IOException {
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/customer.fxml"));
        stage = (Stage) customerButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        CustomerController customerController = loader.getController();
    }

    public void setUserData(User user){
        userData = user;
    }
}
