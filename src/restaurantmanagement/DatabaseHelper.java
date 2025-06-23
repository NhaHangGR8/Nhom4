package restaurantmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Thay bằng username MySQL của bạn
    private static final String PASSWORD = ""; // Thay bằng password MySQL của bạn

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}