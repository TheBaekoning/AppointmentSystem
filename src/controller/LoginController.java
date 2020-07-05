package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.sql.*;
import java.util.Locale;

public class LoginController {
    @FXML
    Button loginButton;
    @FXML
    TextField loginUser;
    @FXML
    TextField loginPassword;

    @FXML

    /**
     * validates log in and matches inputted username and password to what is held in the database.
     */
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
            AppointmentController appointmentController = loader.getController();
            User userData = new User();
            userData.setUsername(result.getString("userName"));
            userData.setUserId(result.getInt("userId"));
            appointmentController.setUserData(userData);
        } else {
            popMessage();
        }
        System.out.println("Closing Connection");
        connection.close();
    }

    /**
    * Pop up message if the username and password does not match to one held in Database. Will currently match US or FR (english / french)
     */
    private void popMessage() {
        Locale currentLocale = Locale.getDefault()   ;
        if (currentLocale.getCountry().equals("US")); {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Incorrect Password");
            alert.setHeaderText("Username And Password Incorrect");
            alert.setContentText("Please Try Again");
            alert.showAndWait();

        };
        if (currentLocale.getCountry().equals("FR")) {
            System.out.println(currentLocale.getCountry());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mot de passe incorrect");
            alert.setHeaderText("Nom d'utilisateur et mot de passe incorrects");
            alert.setContentText("Veuillez réessayer");
            alert.showAndWait();
        }

    }
}
