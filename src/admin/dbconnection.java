package admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class dbconnection {
    private static final String URL = "jdbc:mysql://localhost:3306/faculty_management";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        //connection- an interface in java sql package, connection is the return type of the method
        //getConnection- method name

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch (ClassNotFoundException e) {
            throw  new SQLException("JDBC driver not found ", e);
        }



    }
}

