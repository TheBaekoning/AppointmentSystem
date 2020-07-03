package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.sql.*;

public class LoginController {
    @FXML
    Button loginButton;
    @FXML
    TextField loginUser;
    @FXML
    TextField loginPassword;

    @FXML
    public void loginValidation() throws SQLException, IOException {
        Statement statement;
        boolean loginCheck = false;
        ResultSet result;
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT * FROM user");

        while (result.next()) {
            if (result.getString("userName").equals(loginUser.getText()) &&
                    result.getString("password").equals(loginPassword.getText())) {
                loginCheck = true;
                break;
            }
            loginCheck = false;
        }

        if (loginCheck) {
            System.out.println("LOGIN SUCCESS!");
            Stage stage;
            Parent root;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
            stage = (Stage) loginButton.getScene().getWindow();
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            Appointment appointment = loader.getController();
            User userData = new User();
            userData.setUsername(result.getString("userName"));
            userData.setUserId(result.getInt("userId"));
            appointment.setUserData(userData);
        } else {
            System.out.println("LOGIN FAILURE!");
            /** TODO: add in login error pop up for spanish and english based on location
             *
             */
        }
    }
}
