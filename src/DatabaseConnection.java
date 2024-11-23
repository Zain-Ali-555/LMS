import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/lms"; // Replace `library` with your DB name
    private static final String USER = "root"; // Replace with your DB username
    private static final String PASSWORD = "zain"; // Replace with your DB password

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Return null if connection fails
        }
    }
}
