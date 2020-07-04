package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    @FXML
    Button cancelButton;
    @FXML
    Button addCustomerButton;
    @FXML
    Button updateCustomerButton;
    @FXML
    Button deleteCustomerButton;

    public TableView<Customer> customerTable;
    public TableColumn customerName;
    public TableColumn customerAddress;
    public TableColumn customerPhone;

    private static Boolean isInitialized = false;

    private List<Customer> customerList = new ArrayList<>();
    private final ObservableList<Customer> customerListObservable = FXCollections.observableList(customerList);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            populateTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        customerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        customerTable.setItems(customerListObservable);
    }

    public void cancelButtonClicked() throws IOException {
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
        stage = (Stage) cancelButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void addCustomerClicked() throws IOException {
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/addCustomer.fxml"));
        stage = (Stage) addCustomerButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void updateCustomerClicked() throws IOException {
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/updateCustomer.fxml"));
        stage = (Stage) updateCustomerButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void deleteCustomerClicked() throws SQLException {
        customerTable.getSelectionModel().getSelectedItem();
        Long count = 0L;
        Statement statement;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        /**
         * Used a lambda expression here to find how many times an address duplicated amongst the customers. If so, then
         * just delete the customer and NOT the address. If there isn't a duplicated address we know that it is unique to
         * the selected customer and so we delete the address as well as the customer.
         */
        count = customerList.stream()
                .filter(x -> x.getAddress().equals(customerTable
                        .getSelectionModel()
                        .getSelectedItem()
                        .getAddress())).count();

        if (count > 1) {
            statement = connection.createStatement();
            Integer isDeleted = statement.executeUpdate("DELETE\n" +
                    "FROM customer\n" +
                    "WHERE customerName = " + "'" + customerTable.getSelectionModel().getSelectedItem().getName() + "'");
            if (isDeleted > 0) {
                System.out.println("DELETE SUCCESSFUL!");
                customerListObservable.remove(
                        customerList.stream()
                                .filter(x -> x.getName() == customerTable.getSelectionModel().getSelectedItem().getName())
                                .findFirst().get());
            } else {
                System.out.println("ERROR: Delete not successful!");
            }
        } else {
            statement = connection.createStatement();
            ResultSet result;
            Integer addressId;
            result = statement.executeQuery("SELECT addressId\n" +
                    "FROM customer\n" +
                    "WHERE customerName = " + "'" + customerTable.getSelectionModel().getSelectedItem().getName() + "'");
            result.next();
            addressId = result.getInt("addressId");
            Integer isDeleted = statement.executeUpdate("DELETE\n" +
                    "FROM customer\n" +
                    "WHERE customerName = " + "'" + customerTable.getSelectionModel().getSelectedItem().getName() + "'");
            if (isDeleted > 0)
                System.out.println("CUSTOMER DELETE SUCCESSFUL!");
            else {
                System.out.println("ERROR: Delete not successful!");
            }
            isDeleted = statement.executeUpdate("DELETE\n" +
                    "FROM address\n" +
                    "WHERE addressId = " + addressId );
            if (isDeleted > 0) {
                System.out.println("ADDRESS DELETE SUCCESSFUL!");

                /**
                 * Used a lambda expression here to find an object that may have the addressId needed to remove it from
                 * the customerList in order to update the tableview that shows the object was deleted.
                 */

                customerListObservable.remove(
                        customerList.stream()
                                .filter(x -> x.getName() == customerTable.getSelectionModel().getSelectedItem().getName())
                                .findFirst().get());
            } else {
                System.out.println("ERROR: Delete not successful! (Address)");
                connection.close();
            }

        }
    }

    public void populateTable() throws SQLException {
        Statement statement;
        ResultSet result;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT c.customerName, a.address, a.address2,a.postalCode, n.city, l.country, a.phone\n" +
                "FROM customer AS c\n" +
                "INNER JOIN address AS a ON c.addressId = a.addressId\n" +
                "INNER JOIN city AS n ON a.cityId = n.cityId\n" +
                "INNER JOIN country AS l ON l.countryId = n.countryId;");

        while (result.next()) {
            Customer customer = new Customer();
            String addressSecondary = "";

            if (result.getString("address2") == null) {
                addressSecondary = "";
            } else {
                addressSecondary = result.getString("address2");
            }

            customer.setName(result.getString("customerName"));
            customer.setAddress(result.getString("address"),
                    addressSecondary,
                    result.getString("postalCode"),
                    result.getString("city"),
                    result.getString("country"));
            customerList.add(customer);
        }

        connection.close();
    }
}

