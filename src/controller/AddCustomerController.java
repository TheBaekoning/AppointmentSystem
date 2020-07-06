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

    /**
     * returns to customer menu
     *
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
     * checks if customer name inputted is a valid entry
     *
     * @throws CustomException
     */
    private void checkCustomerName() throws CustomException {
        if (((nameBox.getText().equals("")) && (nameBox.getText() != null))
                || !(Objects.requireNonNull(nameBox.getText()).matches("[a-zA-Z]+\\s[a-zA-Z]+"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Characters for Customer Name");
            alert.setHeaderText("Customer name must have only alphabets and form: <First Name> <Last Name>");
            alert.setContentText("Please enter a valid name and try again");
            alert.showAndWait();
            throw (new CustomException("INVALID CHARACTERS"));
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
     * attempts to add a customer to the database. It will add a new address if the inputted address does
     * not exist
     *
     * @throws SQLException
     * @throws IOException
     * @throws CustomException
     */
    public void addCustomer() throws SQLException, IOException, CustomException {
        checkCustomerName();
        checkPostal();
        checkPhone();
        checkAddress();
        Statement statement;
        ResultSet result, cityResult;
        int cityId;
        TimeConverter timeConverter = new TimeConverter();
        String time = timeConverter.getUtcTime();
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

    /**
     * selects country and will auto populate city based on country
     *
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
     * populates the country drop down to be used
     *
     * @param location
     * @param resources
     */
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
