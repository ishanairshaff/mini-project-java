import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/faculty_management";
    private static final String USER = "root";
    private static final String PASSWORD = "1427";
    private static Connection con;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC Driver loaded.");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't load JDBC driver: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to database.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return con;
    }
}
