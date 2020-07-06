package util;

import javafx.scene.control.Alert;
import model.Appointment;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeConverter {
    /**
     * gets current system local time
     * @return
     */
    public String getLocalTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * gets current UTC time
     * @return
     */
    public String getUtcTime() {
        return LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * converts system time to UTC
     * @param time
     * @return
     */
    public String convertDefaultToUtc(String time) {
        return ZonedDateTime
                .parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()))
                .toOffsetDateTime()
                .atZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * converts UTC to system time
     * @param time
     * @return
     */
    public String convertUtcToDefault(String time) {
        return ZonedDateTime
                .parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC))
                .toOffsetDateTime()
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * checks if date is in a valid form
     * @param validate
     * @throws DateTimeParseException
     */
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

    /**
     * checks if time is within busines hours
     * @param validate
     * @throws Exception
     */
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

    /**
     * checks if date within the current month
     * @param validate
     * @return
     */
    public boolean isCurrentMonth(String validate) {
        String newValidate = LocalDateTime.parse(validate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        return localDate.equals(newValidate);
    }

    /**
     * checks if date is witnin current week
     * @param validate
     * @return
     */
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

    /**
     * converts date and time into ZoneDateTime object
     * @param time
     * @return
     * @throws ParseException
     */
    public ZonedDateTime dateConverter(String time) throws ParseException {
        return ZonedDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
    }

    /**
     * uses converted date time ZoneDateTime object to see if time overlaps
     * @param time1Start
     * @param time1End
     * @param time2Start
     * @param time2End
     * @throws ParseException
     * @throws CustomException
     */
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

    public void loginCheck(List<Appointment> appointment) {
        String currentDayHour;
        String currentTime = getUtcTime();
        String minute = currentTime.substring(14,16);
        currentDayHour = currentTime.substring(0,13);
        int minuteMin = Integer.parseInt(minute);
        int minuteMax = minuteMin + 15;

        appointment.forEach(x -> {
            String appointmentMinute = x.getStartTime().substring(14,16);
            int appointmentMin = Integer.parseInt(appointmentMinute);
            System.out.println(minuteMin + " < = " + appointmentMin + " > = " + minuteMax + " AND " + x.getStartTime().substring(0,13));
            if (x.getStartTime().substring(0,13).equals(currentDayHour) && (
                    minuteMin <= appointmentMin && minuteMax >= appointmentMin
            ) ){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming Appointment Within 15 Minutes");
                alert.setHeaderText("You have an appointment starting in 15 minutes");
                alert.setContentText("Did I mention that an appointment will start within 15 minutes?");
                alert.showAndWait();
            }
        });



        System.out.println(minuteMin + " - " + minuteMax);
        System.out.println(currentDayHour);
        System.out.println(getUtcTime());
    }

}
