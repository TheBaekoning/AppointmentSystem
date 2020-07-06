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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReportController implements Initializable {
    private List<User> userList = new ArrayList<>();
    private final ObservableList<User> userListObservable = FXCollections.observableList(userList);

    private List<Appointment> appointmentList = new ArrayList<>();
    private final ObservableList<Appointment> appointmentObservableList = FXCollections.observableList(appointmentList);


    @FXML
    Button cancelButton;

    public TableView<User> userTable;
    public TableView<Appointment> appointmentTable;

    public TableColumn userColumn;
    public TableColumn nameColumn;
    public TableColumn appointmentColumn;
    public TableColumn typeColumn;

    @FXML
    Text totalText;
    @FXML
    Text differentText;
    @FXML
    Text userText;


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

    public int differentTypeReport() throws SQLException {
        Statement statement;
        ResultSet result;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT COUNT(DISTINCT type)type FROM appointment");
        result.next();
        int resultInt = result.getInt("type");
        connection.close();
        return resultInt;
    }

    public int totalAppointments() throws SQLException {
        Statement statement;
        ResultSet result;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT COUNT(distinct appointmentId)type FROM appointment");
        result.next();
        int resultInt = result.getInt("type");
        connection.close();
        return resultInt;
    }

    public void createSchedule() throws SQLException {
        appointmentList.clear();
        appointmentObservableList.clear();
        Statement statement;
        ResultSet result;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT * FROM appointment WHERE userId = " + userTable
                .getSelectionModel().getSelectedItem().getUserId());

        while (result.next()) {
            Appointment appointment = new Appointment();

        //    appointment.setName(result.getString("customerName"));
        //    appointment.setCustomerId(result.getInt("customerId"));
        //    appointment.setUserId(result.getInt("userId"));
            appointment.setAppointmentId(result.getInt("appointmentId"));
            appointment.setType(result.getString("type"));
            appointment.setStartTime(result.getString("start"));
            appointment.setEndTime(result.getString("end"));
            appointmentList.add(appointment);
            System.out.println(appointmentObservableList.size());
        }

        connection.close();
    }

    public void generateReport() throws SQLException {
        differentText.setText("Total Different Types Of Appointments: " + differentTypeReport());
        totalText.setText("Total Number Of Appointments In Database: " + totalAppointments());
        createSchedule();


        appointmentColumn.setCellValueFactory(new PropertyValueFactory<>("appointment"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        appointmentTable.setItems(appointmentObservableList);
    }

    public void populateList() throws SQLException {
        Statement statement;
        ResultSet result;

        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT * FROM user");

        while (result.next()) {
            User user = new User();

            user.setUsername(result.getString("userName"));
            user.setUserId(result.getInt("userId"));

            userList.add(user);
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



        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        userTable.setItems(userListObservable);
    }
}
