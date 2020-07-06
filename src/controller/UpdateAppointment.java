package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import util.CustomException;
import util.TimeConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateAppointment implements Initializable {
    @FXML
    Button cancelButton;
    @FXML
    Button updateButton;
    @FXML
    TextField startBox;
    @FXML
    TextField endBox;
    @FXML
    TextField typeBox;

    List<Appointment> compareList = new ArrayList<>();
    Appointment appointment = new Appointment();

    TimeConverter time = new TimeConverter();

    /**
     * returns to appointment menu
     * @throws IOException
     */
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

    /**
     * used to set appointment that was selected
     * @param appointment
     */
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    /**
     * updates appointment based on input values. This will throw an error and message if date is invalid or overlapping
     * @throws SQLException
     * @throws IOException
     */
    public void onClickUpdate() throws SQLException, IOException {
        try {
            for (int i = 0; i < compareList.size(); i++)
                time.isOverlap(startBox.getText(), endBox.getText(), compareList.get(i).getStartTime(), compareList.get(i).getEndTime());

            time.isValidDate(startBox.getText());
            time.isValidDate(endBox.getText());
            Statement statement;
            Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                    "U0600d", "53688664081");

            statement = connection.createStatement();
            if (!typeBox.getText().isEmpty()) {
                statement.execute("UPDATE appointment \n" +
                        "SET type = '" + typeBox.getText() + "' " +
                        "WHERE appointmentId = " + appointment.getAppointmentId() + " ;");
            }

            if (!startBox.getText().isEmpty()) {
                statement.execute("UPDATE appointment \n" +
                        "SET start = '" + time.convertDefaultToUtc(startBox.getText()) + "' " +
                        "WHERE appointmentId = " + appointment.getAppointmentId() + " ;");
            }

            if (!endBox.getText().isEmpty()) {
                statement.execute("UPDATE appointment \n" +
                        "SET end = '" + time.convertDefaultToUtc(endBox.getText()) + "' " +
                        "WHERE appointmentId = " + appointment.getAppointmentId() + " ;");
            }

            statement.execute("UPDATE appointment \n" +
                    "SET lastUpdateBy = '" + AppointmentController.userData.getUsername() + "', lastUpdate = '" + time.getUtcTime() + "' " +
                    "WHERE appointmentId = " + appointment.getAppointmentId() + " ;");
            cancelButtonClicked();
        } catch (ParseException | CustomException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Overlapping Time");
            alert.setHeaderText("Start Time Overlaps With Existing appointment");
            alert.setContentText("Please enter a valid time and try again");
            alert.showAndWait();
        }

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
                // appointmentList.add(appointment);
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void setCompareList(List<Appointment> appointments) {
        compareList.addAll(appointments);
    }
}
