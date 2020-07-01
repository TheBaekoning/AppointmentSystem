package controller;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class LoginController {
    Driver driver;
    Properties properties;
    DriverManager driverManager;

    public void loginValidation() throws SQLException {
        driverManager = null;
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d" +
                "?user=U0600d&password=53688664081");
    }

}
