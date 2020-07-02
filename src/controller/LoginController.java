package controller;

import java.sql.*;
import java.util.Properties;

public class LoginController {
    Driver driver;
    Properties properties;

    public void loginValidation() throws SQLException, ClassNotFoundException {
        Statement statement;
        ResultSet result;
        Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U0600d",
                "U0600d", "53688664081");

        statement = connection.createStatement();
        result = statement.executeQuery("SELECT * FROM user");
        result.next();
        String resultString = result.getString("userName");
        System.out.println(resultString);

    }

}
