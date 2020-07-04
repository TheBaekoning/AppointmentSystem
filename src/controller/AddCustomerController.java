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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
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

    public void addCustomer() throws SQLException, IOException {
        Statement statement;
        ResultSet result, cityResult;
        int cityId;
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");
        statement = connection.createStatement();

        cityResult = statement.executeQuery("SELECT cityId\n" +
                "FROM city\n" +
                "WHERE city = '" + cityDropDown.getSelectionModel().getSelectedItem().toString() + "'");

        cityResult.next();
        cityId = cityResult.getInt("cityId");

        result = statement.executeQuery("SELECT addressId FROM address " +
                "WHERE address = '" + addressBox.getText() + "' AND address2 = '" + address2Box.getText()
                + "' AND postalCode = '" + zipBox.getText() + "' AND phone = '" + phoneBox.getText() + "' AND cityId =" + cityId);
        if (!result.next()) {
            statement.execute("INSERT INTO address(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)" +
                    "VALUES( '" + addressBox.getText() + "', '" +
                    address2Box.getText() + "', " + cityId + ", '" + zipBox.getText() + "', '" + phoneBox.getText() +
                    "', '" + time + "', '" + AppointmentController.userData.getUsername() + "', '" + time + "', '" + AppointmentController.userData.getUsername() + "')");

            result = statement.executeQuery("SELECT addressId FROM address " +
                    "WHERE address = '" + addressBox.getText() + "' AND address2 = '" + address2Box.getText() + "'");
            result.next();

            statement.execute("INSERT INTO customer(CUSTOMERNAME, ADDRESSID, ACTIVE," +
                    " CREATEDATE, CREATEDBY, LASTUPDATE, LASTUPDATEBY)\n" +
                    "VALUES " + "('" + nameBox.getText() + "', " + result.getInt("addressId") + ", " + "1" + ", '" +
                    time + "', '" + AppointmentController.userData.getUsername() + "', '" + time + "', '" +
                    AppointmentController.userData.getUsername() + "');");
        } else {
            statement.execute("INSERT INTO customer(CUSTOMERNAME, ADDRESSID, ACTIVE," +
                    " CREATEDATE, CREATEDBY, LASTUPDATE, LASTUPDATEBY)\n" +
                    "VALUES " + "('" + nameBox.getText() + "', " + "1" + ", " + "1" + ", '" +
                    time + "', '" + AppointmentController.userData.getUsername() + "', '" + time + "', '" +
                    AppointmentController.userData.getUsername() + "');");
        }
        connection.close();
        cancelButtonClicked();

    }

    public void clickedCountryDropDown() throws SQLException {
        if (!cityList.isEmpty()) {
            cityList.clear();
        }
        Statement statement;
        ResultSet result, countryIdResult;
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");
        statement = connection.createStatement();
        countryIdResult = statement.executeQuery("SELECT * FROM country WHERE country = '"
                + countryDropDown.getSelectionModel().getSelectedItem().toString() + "'");
        countryIdResult.next();
        result = statement.executeQuery("SELECT * FROM city WHERE countryId = " + countryIdResult.getInt("countryId"));

        while (result.next()) {
            cityList.add(result.getString("city"));
        }
        ObservableList cityListObservable = FXCollections.observableList(cityList);
        cityDropDown.setItems(cityListObservable);
        connection.close();
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
