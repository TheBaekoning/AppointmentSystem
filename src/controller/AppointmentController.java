package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.User;
import util.TimeConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    public static User userData;
    @FXML
    Button deleteButton;
    @FXML
    Button addButton;
    @FXML
    Button updateButton;
    @FXML
    Button customerButton;
    @FXML
    Text timeLabel;

    public TableView<Appointment> appointmentTable;
    public TableColumn nameColumn;
    public TableColumn appointmentColumn;
    public TableColumn typeColumn;

    private List<Appointment> compareList = new ArrayList<>();
    private List<Appointment> appointmentList = new ArrayList<>();
    private final ObservableList<Appointment> appointmentObservableList = FXCollections.observableList(appointmentList);

    /**
     * used for converting utc/local and weekly/monthly views
     * *********************
     */
    boolean isUtc = true;
    boolean isLocal = false;
    boolean isMonthly;
    boolean isWeekly;
    boolean isAll;

    /**
     * ***********************
     */

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
        addAppointment.setCompareList(compareList);
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
        updateAppointment.setAppointment(appointmentTable.getSelectionModel().getSelectedItem());
        updateAppointment.setCompareList(compareList);
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

    public void onDeleteClicked() throws SQLException {
        int id = appointmentTable.getSelectionModel().getSelectedItem().getAppointmentId();

        Statement statement;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        statement.execute("DELETE FROM appointment WHERE appointmentId = " + id + ";");

        appointmentObservableList.remove(appointmentList
                .stream().filter(x -> x.getAppointmentId() == id).findFirst().get());

    }

    public void customerDetailButtonClicked() throws SQLException {
        Customer customer = new Customer();
        Statement statement;
        ResultSet result;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT * FROM " +
                "( SELECT a.address, a.address2, a.postalCode, a.phone, c.customerId, c.customerName, c2.city, c3.country " +
                "FROM customer AS c " +
                "INNER JOIN address a on c.addressId = a.addressId " +
                "INNER JOIN city c2 on a.cityId = c2.cityId " +
                "INNER JOIN country c3 on c2.countryId = c3.countryId) as ca " +
                "WHERE customerId = " + appointmentTable.getSelectionModel().getSelectedItem().getCustomerId());
        result.next();

        customer.setName(result.getString("customerName"));
        customer.setAddress(result.getString("address"),
                result.getString("address2"),
                result.getString("postalCode"),
                result.getString("city"),
                result.getString("country"));
        customer.setPhoneNumber(result.getString("phone"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Details");
        alert.setHeaderText(customer.getName());
        alert.setContentText("Address: " + customer.getAddress() + "\n" +
                "Phone: " + customer.getPhoneNumber());
        alert.showAndWait();
    }

    public void setUserData(User user) {
        userData = user;
    }

    public void populateList() throws SQLException {
        Statement statement;
        ResultSet result;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT a.appointmentId, a.customerId, a.userId, a.type, a.start, a.end, c.customerName\n" +
                "FROM appointment AS a\n" +
                "INNER JOIN customer c on a.customerId = c.customerId");

        while (result.next()) {
            Appointment appointment = new Appointment();

            appointment.setName(result.getString("customerName"));
            appointment.setCustomerId(result.getInt("customerId"));
            appointment.setUserId(result.getInt("userId"));
            appointment.setAppointmentId(result.getInt("appointmentId"));
            appointment.setType(result.getString("type"));
            appointment.setStartTime(result.getString("start"));
            appointment.setEndTime(result.getString("end"));
            appointmentList.add(appointment);
        }

        connection.close();

    }

    public void clickLocal() {
        if (!isLocal) {
            List<Appointment> tempList = new ArrayList<>(appointmentList);
            appointmentObservableList.clear();
            appointmentList.clear();

            tempList.forEach(x -> {
                x.setStartTime(new TimeConverter().convertUtcToDefault(x.getStartTime()));
                x.setEndTime(new TimeConverter().convertUtcToDefault(x.getEndTime()));
                x.setAppointment();
                appointmentList.add(x);
            });
            isUtc = false;
            isLocal = true;

            timeLabel.setText("Current Time Zone: Local");
        }
    }

    public void clickUtc() {
        if (!isUtc) {
            List<Appointment> tempList = new ArrayList<>(appointmentList);
            appointmentObservableList.clear();
            appointmentList.clear();

            tempList.forEach(x -> {
                x.setStartTime(new TimeConverter().convertDefaultToUtc(x.getStartTime()));
                x.setEndTime(new TimeConverter().convertDefaultToUtc(x.getEndTime()));
                x.setAppointment();
                appointmentList.add(x);
            });
            isUtc = true;
            isLocal = false;

            timeLabel.setText("Current Time Zone: UTC");
        }
    }

    public void appointmentWeek() throws SQLException {
        clickUtc();
        List<Appointment> tempList = new ArrayList<>();
        TimeConverter time = new TimeConverter();
        appointmentObservableList.clear();
        appointmentList.clear();

        populateList();

        appointmentList.forEach(x -> {
            if (time.isCurrentWeek(x.getStartTime()))
                tempList.add(x);
        });

        appointmentList.clear();
        appointmentObservableList.clear();

        appointmentList.addAll(tempList);
    }

    public void appointmentMonth() throws SQLException {
        clickUtc();
        List<Appointment> tempList = new ArrayList<>();
        TimeConverter time = new TimeConverter();
        appointmentObservableList.clear();
        appointmentList.clear();

        populateList();

        appointmentList.forEach(x -> {
            if (time.isCurrentMonth(x.getStartTime()))
                tempList.add(x);
        });

        appointmentList.clear();
        appointmentObservableList.clear();

        appointmentList.addAll(tempList);
    }

    public void appointmentAll() throws SQLException {
        clickUtc();
        appointmentList.add(new Appointment());
        appointmentObservableList.clear();
        appointmentList.clear();
        populateList();
    }

    public void clickReport() throws IOException {
        System.out.println(userData.getUsername());
        Stage stage;
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/report.fxml"));
        stage = (Stage) addButton.getScene().getWindow();
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            populateList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        compareList.addAll(appointmentList);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        appointmentColumn.setCellValueFactory(new PropertyValueFactory<>("appointment"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        appointmentTable.setItems(appointmentObservableList);
    }
}
