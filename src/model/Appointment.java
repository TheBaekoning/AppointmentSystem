package model;

public class Appointment {
    private String name;
    private String appointment;
    private String startTime;
    private String endTime;
    private String type;
    private int appointmentId;
    private int customerId;
    private int userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppointment() {
        setAppointment();
        return appointment;
    }

    public void setAppointment() {
        appointment = startTime.toString() + " - " + endTime.toString();
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public int getUserId() {
        return userId;
    }
}
