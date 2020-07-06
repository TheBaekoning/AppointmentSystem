package util;

import javafx.scene.control.Alert;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.Date;
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

    public boolean isCurrentMonth(String validate) {
        String newValidate = LocalDateTime.parse(validate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        return localDate.equals(newValidate);
    }

    public boolean isCurrentWeek(String validate) {
        int day, month, year; // comparing dates
        int cDay, cMonth, cYear; // current dates
        String currentDate = getLocalTime();

        year = Integer.parseInt(validate.substring(0,4));
        month = Integer.parseInt(validate.substring(5,7));
        day = Integer.parseInt(validate.substring(8,10));

        cYear = Integer.parseInt(currentDate.substring(0,4));
        cMonth = Integer.parseInt(currentDate.substring(5,7));
        cDay = Integer.parseInt(currentDate.substring(8,10));

        Locale userLocale = Locale.getDefault();
        WeekFields weekNumbering = WeekFields.of(userLocale);

        LocalDate date = LocalDate.of(year, month, day);
        LocalDate cDate = LocalDate.of(cYear, cMonth, cDay);
        int compWeek = date.get(weekNumbering.weekOfWeekBasedYear());
        int currentWeek = cDate.get(weekNumbering.weekOfWeekBasedYear());

        return compWeek == currentWeek;
    }

    private ZonedDateTime dateConverter(String time) throws ParseException {
        return ZonedDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
    }

    public void isOverlap(String time1Start, String time1End, String time2Start, String time2End) throws ParseException, CustomException {
        ZonedDateTime date1;
        ZonedDateTime date2;
        ZonedDateTime date3;
        ZonedDateTime date4;

        date1 = dateConverter(time1Start);
        date2 = dateConverter(time1End);
        date3 = dateConverter(time2Start);
        date4 = dateConverter(time2End);
        if(date1.isBefore(date4) && date3.isBefore(date2))
            throw(new CustomException("OVERLAPPING TIME"));
    }
}
