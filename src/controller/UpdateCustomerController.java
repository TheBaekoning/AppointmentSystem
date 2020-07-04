package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable {
    @FXML
    Button cancelButton;
    @FXML
    TextField nameBox;
    @FXML
    TextField addressBox;
    @FXML
    TextField address2Box;
    @FXML
    TextField phoneBox;
    @FXML
    ComboBox countryDropDown;
    @FXML
    ComboBox cityDropDown;
    @FXML
    TextField zipBox;

    private List<String> countryList = new ArrayList<>();
    private List<String> cityList = new ArrayList<>();

    public void cancelButtonClicked() throws IOException {
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/customer.fxml"));
        stage = (Stage) cancelButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Statement statement;
        ResultSet result;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                    "U0600d", "53688664081");
            statement = connection.createStatement();
            result = statement.executeQuery("SELECT * FROM country");

            while (result.next()) {
                countryList.add(result.getString("country"));
            }
            ObservableList countryListObservable = FXCollections.observableList(countryList);
            countryDropDown.setItems(countryListObservable);
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
