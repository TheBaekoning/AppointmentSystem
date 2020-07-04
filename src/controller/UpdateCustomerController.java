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
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable {
    Integer customerId;
    String address2 = "";
    String zipCode = "";
    String phoneNumber = "";
    String address = "";
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
    Customer customer;

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

    public void setCustomer(Customer customer) throws SQLException {

        ResultSet addressResult, idResult;
        Statement statement;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");
        statement = connection.createStatement();
        addressResult = statement.executeQuery("SELECT address2, address, phone, postalCode FROM address " +
                "WHERE addressId = (SELECT addressId FROM customer WHERE customerName = '"
                + customer.getName() + "')");
        addressResult.next();
        address = addressResult.getString("address");
        address2 = addressResult.getString("address2");
        zipCode = addressResult.getString("postalCode");
        phoneNumber = addressResult.getString("phone");

        this.customer = customer;


        nameBox.setText(customer.getName());
        addressBox.setText(address);
        address2Box.setText(address2);
        phoneBox.setText(phoneNumber);
        zipBox.setText(zipCode);

        idResult = statement.executeQuery("SELECT customerId FROM customer WHERE customerName = '" +
                customer.getName() + "'");

        idResult.next();
        customerId = idResult.getInt("customerId");

        System.out.println(customerId);

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
