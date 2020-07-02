package model;

import java.time.LocalDateTime;

public class Appointment {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String type;

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }
}
