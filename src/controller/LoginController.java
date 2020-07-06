package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import util.TimeConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    Button loginButton;
    @FXML
    TextField loginUser;
    @FXML
    TextField loginPassword;
    @FXML
    Text userText;
    @FXML
    Text passwordText;
    boolean isFrench = false;

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

            try {
                File log = new File("user_login_logs.txt");
                if (log.createNewFile()) {
                    System.out.println("File created: " + log.getName());
                    try {
                        FileWriter fileWriter = new FileWriter("user_login_logs.txt");
                        fileWriter.write("USER: " + userData.getUsername() + " LOGGED AT: " + new TimeConverter().getUtcTime());
                        fileWriter.close();
                        System.out.println("Successfully wrote to the file.");
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("File already exists.");
                    try {
                        FileWriter fileWriter = new FileWriter("user_login_logs.txt", true);
                        fileWriter.write("\nUSER: " + userData.getUsername() + " LOGGED AT: " + new TimeConverter().getUtcTime());
                        fileWriter.close();
                        System.out.println("Successfully wrote to the file.");
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
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
        Locale currentLocale = Locale.getDefault();
        if (currentLocale.getCountry().equals("US")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Incorrect Password");
            alert.setHeaderText("Username And Password Incorrect");
            alert.setContentText("Please Try Again");
            alert.showAndWait();

        }
        if (currentLocale.getCountry().equals("FR")) {
            System.out.println(currentLocale.getCountry());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mot de passe incorrect");
            alert.setHeaderText("Nom d'utilisateur et mot de passe incorrects");
            alert.setContentText("Veuillez réessayer");
            alert.showAndWait();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Locale currentLocale = Locale.getDefault();
        if (currentLocale.getCountry().equals("FR")) {
            userText.setText("Nom d'utilisateur");
            passwordText.setText("mot de passe");
            loginButton.setText("s'identifier");

        }

    }
}
