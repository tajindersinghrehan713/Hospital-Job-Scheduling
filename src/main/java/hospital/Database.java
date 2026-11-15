
package hospital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hospital_scheduling";

    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con.isValid(2);
        } catch (SQLException e) {
            System.out.println("Database connection failed: "
                    + e.getMessage());
            return false;
        }
    }
}
