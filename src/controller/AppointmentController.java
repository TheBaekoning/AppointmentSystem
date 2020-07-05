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
import model.Appointment;
import model.Customer;
import model.User;

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
    public TableView<Appointment> appointmentTable;
    public TableColumn nameColumn;
    public TableColumn appointmentColumn;
    public TableColumn typeColumn;

    private List<Appointment> appointmentList = new ArrayList<>();
    private final ObservableList<Appointment> appointmentObservableList = FXCollections.observableList(appointmentList);


    /**
     * used for converting utc/local and weekly/monthly views
     * *********************
     */
    boolean isUtc;
    boolean isLocal;
    boolean isMonthly;
    boolean isWeekly;
    boolean isAll;

    /**
     ************************
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

    public void customerDetailButtonClicked() {

    }

    public void setUserData(User user){
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            populateList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        appointmentColumn.setCellValueFactory(new PropertyValueFactory<>("appointment"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        appointmentTable.setItems(appointmentObservableList);
    }
}
