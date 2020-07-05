package util;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import javafx.scene.control.Alert;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class TimeConverter {
    public String getLocalTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    public String getUtcTime() {
        return LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    public String convertDefaultToUtc(String time) {
        return ZonedDateTime
                .parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()))
                .toOffsetDateTime()
                .atZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String convertUtcToDefault(String time) {
        return ZonedDateTime
                .parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC))
                .toOffsetDateTime()
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void isValidDate(String validate) throws DateTimeParseException {
        try {
            LocalDateTime.parse(validate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Time String");
            alert.setHeaderText("Start/End Times Must Be In: yyyy-MM-dd hh:mm:ss");
            alert.setContentText("Please enter a valid time and try again");
            alert.showAndWait();
        }
    }

    public void isBusinessHours(String validate) throws Exception {
        int compare = Integer.parseInt(LocalDateTime.parse(validate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("HH")));
        int opening = Integer.parseInt(LocalTime.parse("09:00:00", DateTimeFormatter.ofPattern("HH:mm:ss")).format(DateTimeFormatter.ofPattern("HH")));
        int closing = Integer.parseInt(LocalTime.parse("17:00:00", DateTimeFormatter.ofPattern("HH:mm:ss")).format(DateTimeFormatter.ofPattern("HH")));
        if (compare < opening || compare > closing){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Outside of Business Hours");
            alert.setHeaderText("Start/End Times Must Be Between the hours of: 09:00:00 - 17:00:00");
            alert.setContentText("Please enter a valid time and try again");
            alert.showAndWait();
            throw new Exception("Outside of Business Hours");
        }
    }
}
