package restaurantmanagement; // Hoặc package chứa DatabaseHelper của bạn

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminAccountCreator {

    public static void main(String[] args) {
        String adminUsername = "admin"; // Tên đăng nhập admin
        String adminPassword = "admin123"; // Mật khẩu admin (KHUYẾN NGHỊ: HASH MẬT KHẨU TRONG ỨNG DỤNG THỰC TẾ)
        String adminEmail = "admin@gr8restaurant.com";
        String adminPhone = "0123456789";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseHelper.getConnection();
            String sql = "INSERT INTO users (username, password, role, email, phone_number) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, adminUsername);
            pstmt.setString(2, adminPassword); // Trong ứng dụng thực tế, hãy sử dụng băm mật khẩu (e.g., BCrypt)
            pstmt.setString(3, "admin"); // Đặt vai trò là 'admin'
            pstmt.setString(4, adminEmail);
            pstmt.setString(5, adminPhone);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tài khoản admin '" + adminUsername + "' đã được tạo thành công.");
            } else {
                System.out.println("Tạo tài khoản admin thất bại.");
            }
        } catch (SQLException ex) {
            // Xử lý lỗi khi tên đăng nhập hoặc email đã tồn tại
            if (ex.getErrorCode() == 1062) { // Mã lỗi MySQL cho mục nhập trùng lặp
                System.err.println("Lỗi: Tên đăng nhập hoặc Email đã tồn tại. Vui lòng chọn tên khác hoặc kiểm tra lại.");
            } else {
                System.err.println("Lỗi CSDL khi tạo tài khoản admin: " + ex.getMessage());
                ex.printStackTrace();
            }
        } finally {
            DatabaseHelper.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}