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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import util.TimeConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddAppointment implements Initializable {
    @FXML
    Button cancelButton;
    @FXML
    Button addButton;
    @FXML
    TextField startBox;
    @FXML
    TextField endBox;
    @FXML
    TextField typeBox;

    List<Appointment> appointmentList = new ArrayList<>();
    private final ObservableList<Appointment> appointmentObservableList = FXCollections.observableList(appointmentList);

    public TableView<Appointment> customerTable;
    public TableColumn nameColumn;

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

    public void onClickAddAppointment() throws SQLException, IOException {
        TimeConverter time = new TimeConverter();
        Statement statement;
        Appointment selectedAppointment = new Appointment();
        selectedAppointment = customerTable.getSelectionModel().getSelectedItem();
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        statement.execute("INSERT INTO appointment(customerId, userId, type, start, end, createDate, createdBy, lastUpdate, lastUpdateBy, title, description, location, contact, url)\n" +
                "VALUES(" + selectedAppointment.getCustomerId() + ", " + AppointmentController.userData.getUserId()
                + ", '" + typeBox.getText() + "', '" + startBox.getText() + "', '" + endBox.getText()
                + "', '" + time.getUtcTime() + "', '" + AppointmentController.userData + "', '"
                + time.getUtcTime() + "', '" + AppointmentController.userData
                + "', '" + "blank" + "', '" + "blank" + "', '" + "blank" + "', '" + "blank"  + "', '" + "blank" + "');");
        cancelButtonClicked();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Statement statement;
        ResultSet result;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                    "U0600d", "53688664081");

            statement = connection.createStatement();
            result = statement.executeQuery("SELECT * " +
                    "FROM customer");


            while (result.next()) {
                Appointment appointment = new Appointment();

                appointment.setName(result.getString("customerName"));
                appointment.setCustomerId(result.getInt("customerId"));

                appointmentList.add(appointment);
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        customerTable.setItems(appointmentObservableList);

    }
}
