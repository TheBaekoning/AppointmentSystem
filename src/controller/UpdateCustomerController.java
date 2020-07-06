package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;
import util.CustomException;
import util.TimeConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    /**
     * returns to customer menu
     * @throws IOException
     */
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

    /**
     * selection drop down menu for country
     * @throws SQLException
     */
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

    /**
     * sets the text boxes to reflect selected customer that will be updated
     * @param customer
     * @throws SQLException
     */
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


    }

    /**
     * checks if customer name inputted is a valid entry
     *
     * @throws CustomException
     */
    private void checkCustomerName() throws CustomException, SQLException {
        if (((nameBox.getText().equals("")) && (nameBox.getText() != null))
                || !(Objects.requireNonNull(nameBox.getText()).matches("[a-zA-Z]+\\s[a-zA-Z]+"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Characters for Customer Name");
            alert.setHeaderText("Customer name must have only alphabets and form: <First Name> <Last Name>");
            alert.setContentText("Please enter a valid name and try again");
            alert.showAndWait();
            throw (new CustomException("INVALID CHARACTERS"));
        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");
        Statement statement;
        statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT customerName, customerId " +
                "FROM customer WHERE customerName = '" + nameBox.getText() + "'");

        if(!result.next()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Customer is Non-Existent");
            alert.setHeaderText("Customer does not exist, please check that name is correct and try again");
            alert.setContentText("Please enter a valid name and try again");
            alert.showAndWait();
            throw (new CustomException("CUSTOMER DOES NOT EXIST"));
        } else {
            customerId = result.getInt("customerId");
        }

    }

    private void checkPostal() throws CustomException {
        if (((zipBox.getText().equals("")) && (zipBox.getText() != null))
                || !(Objects.requireNonNull(zipBox.getText()).matches("[0-9]+"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Characters for Postal Code");
            alert.setHeaderText("Postal name must have only numbers");
            alert.setContentText("Please enter a valid postal and try again");
            alert.showAndWait();
            throw (new CustomException("INVALID CHARACTERS"));
        }
    }

    private void checkPhone() throws CustomException {
        if (((phoneBox.getText().equals("")) && (phoneBox.getText() != null))
                || !(Objects.requireNonNull(phoneBox.getText()).matches
                ("^\\d{3}([\\s-])\\d{4}$"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Characters for Phone");
            alert.setHeaderText("Postal name must have only numberse in form 222-3333");
            alert.setContentText("Please enter a valid phone and try again");
            alert.showAndWait();
            throw (new CustomException("INVALID CHARACTERS AND FORM"));
        }
    }

    private void checkAddress() throws CustomException {
        if ((addressBox.getText().equals("")) || (addressBox.getText() == null)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Adresss cannot be blank");
            alert.setHeaderText("There needs to be a value for address");
            alert.setContentText("Please enter a valid address and try again");
            alert.showAndWait();
            throw (new CustomException("Address is empty"));
        }
    }

    /**
     * will attempt to update customer into the data base
     * @throws SQLException
     * @throws IOException
     */
    public void clickedUpdateButton() throws SQLException, IOException, CustomException {
        checkCustomerName();
        checkPostal();
        checkPhone();
        checkAddress();
        String time = new TimeConverter().getUtcTime();
        Statement statement;
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");
        statement = connection.createStatement();


        if (!customer.getName().equals(nameBox.getText())){
            System.out.println("NAME");
            statement.execute("UPDATE customer\n" +
                    "SET customerName = '" + nameBox.getText() + "', lastUpdate = '" + time + "', lastUpdateBy = '" + AppointmentController.userData.getUsername()  +
                    "' WHERE customerId =" + customerId + ";");
        }

        if (!address.equals(addressBox.getText())){
            System.out.println("address");
            statement.execute("UPDATE address\n" +
                    "SET address = '" + addressBox.getText() + "', lastUpdate = '" + time + "', lastUpdateBy = '" + AppointmentController.userData.getUsername()  +
                    "' WHERE addressId = (SELECT addressId FROM customer WHERE customerId = " + customerId + ");");
        }

        if (!address2.equals(address2Box.getText())){
            System.out.println("address2");
            statement.execute("UPDATE address\n" +
                    "SET address2 = '" + address2Box.getText() + "', lastUpdate = '" + time + "', lastUpdateBy = '" + AppointmentController.userData.getUsername()  +
                    "' WHERE addressId = (SELECT addressId FROM customer WHERE customerId = " + customerId + ");");
        }

        if (!phoneNumber.equals(phoneBox.getText())){
            System.out.println("Phone");
            statement.execute("UPDATE address\n" +
                    "SET phone = '" + phoneBox.getText() + "', lastUpdate = '" + time + "', lastUpdateBy = '" + AppointmentController.userData.getUsername()  +
                    "' WHERE addressId = (SELECT addressId FROM customer WHERE customerId = " + customerId + ");");
        }

        if (!zipCode.equals(zipBox.getText())){
            System.out.println("zip");
            statement.execute("UPDATE address\n" +
                    "SET postalCode = '" + zipBox.getText() + "', lastUpdate = '" + time + "', lastUpdateBy = '" + AppointmentController.userData.getUsername()  +
                    "' WHERE addressId = (SELECT addressId FROM customer WHERE customerId = " + customerId + ");");
        }

        int cityId;
        ResultSet cityResult = statement.executeQuery("SELECT cityId\n" +
                "FROM city\n" +
                "WHERE city = '" + cityDropDown.getSelectionModel().getSelectedItem().toString() + "'");

        cityResult.next();
        cityId = cityResult.getInt("cityId");


        statement.execute("UPDATE address\n" +
                "SET cityId = '" + cityId + "', lastUpdate = '" + time + "', lastUpdateBy = '" + AppointmentController.userData.getUsername()  +
                "' WHERE addressId = (SELECT addressId FROM customer WHERE customerId = " + customerId + ");");
        cancelButtonClicked();
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
