package restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterPage extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton registerButton;
    private JLabel messageLabel;
    private Main mainFrame; // Reference to Main frame to switch CardLayout

    public RegisterPage(Main mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 240, 245)); // Light blueish-gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Đăng Ký Tài Khoản Mới", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(41, 128, 185)); // Darker blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(confirmPasswordField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(emailField, gbc);

        // Phone
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(phoneField, gbc);

        // Register Button
        registerButton = new JButton("Đăng Ký");
        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        registerButton.setBackground(new Color(46, 204, 113)); // Emerald Green
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Padding
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10); // More padding above button
        add(registerButton, gbc);

        // Message Label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 10, 10);
        add(messageLabel, gbc);

        // Back to Login Link
        JButton backToLoginButton = new JButton("Quay lại Đăng nhập");
        backToLoginButton.setFont(new Font("Arial", Font.ITALIC, 14));
        backToLoginButton.setForeground(new Color(52, 152, 219)); // Muted Blue
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setOpaque(false);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 10, 10, 10);
        add(backToLoginButton, gbc);

        // Action Listener for Register Button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        // Action Listener for Back to Login Button
        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showPanel("Login");
            }
        });
    }

    private void registerUser() {
        System.out.println("DEBUG: registerUser method called."); // DEBUG PRINT

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showMessage("Vui lòng điền đầy đủ tất cả các trường.", Color.RED);
            System.out.println("DEBUG: Lỗi - Thông tin rỗng."); // DEBUG PRINT
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Mật khẩu xác nhận không khớp.", Color.RED);
            System.out.println("DEBUG: Lỗi - Mật khẩu không khớp."); // DEBUG PRINT
            return;
        }
        
        if (password.length() < 6) {
            showMessage("Mật khẩu phải có ít nhất 6 ký tự.", Color.RED);
            System.out.println("DEBUG: Lỗi - Mật khẩu quá ngắn."); // DEBUG PRINT
            return;
        }

        if (!isValidEmail(email)) {
            showMessage("Địa chỉ email không hợp lệ.", Color.RED);
            System.out.println("DEBUG: Lỗi - Email không hợp lệ."); // DEBUG PRINT
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            showMessage("Số điện thoại không hợp lệ (chỉ chấp nhận số và độ dài 10-11 số).", Color.RED);
            System.out.println("DEBUG: Lỗi - Số điện thoại không hợp lệ."); // DEBUG PRINT
            return;
        }

        System.out.println("DEBUG: Input validation passed. Attempting database connection."); // DEBUG PRINT

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseHelper.getConnection();
            System.out.println("DEBUG: Database connection successful."); // DEBUG PRINT

            // LƯU Ý QUAN TRỌNG: Bạn cần đảm bảo cột 'role' tồn tại trong bảng 'users'
            // và bạn đang chèn giá trị cho nó.
            String sql = "INSERT INTO users (username, password, email, phone_number, role) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Trong ứng dụng thực tế, hãy băm mật khẩu!
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, "user"); // Gán vai trò mặc định là 'user'

            System.out.println("DEBUG: Executing SQL insert statement."); // DEBUG PRINT
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("DEBUG: SQL insert statement executed. Rows affected: " + rowsAffected); // DEBUG PRINT

            if (rowsAffected > 0) {
                showMessage("Đăng ký tài khoản thành công!", new Color(39, 174, 96)); // Success Green
                JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công! Vui lòng đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPanel("Login"); // Redirect to login page after successful registration
                System.out.println("DEBUG: Registration successful. Redirecting to Login."); // DEBUG PRINT
            } else {
                showMessage("Đăng ký tài khoản thất bại.", Color.RED);
                System.out.println("DEBUG: Registration failed (0 rows affected)."); // DEBUG PRINT
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                showMessage("Tên đăng nhập hoặc Email đã tồn tại. Vui lòng chọn tên khác.", Color.RED);
                System.out.println("DEBUG: Lỗi SQL - Tên đăng nhập/Email đã tồn tại: " + ex.getMessage()); // DEBUG PRINT
            } else {
                System.err.println("Lỗi CSDL khi đăng ký người dùng: " + ex.getMessage());
                ex.printStackTrace();
                showMessage("Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), Color.RED);
                System.out.println("DEBUG: Lỗi SQL tổng quát: " + ex.getMessage()); // DEBUG PRINT
            }
        } finally {
            DatabaseHelper.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("DEBUG: Lỗi khi đóng PreparedStatement: " + ex.getMessage()); // DEBUG PRINT
            }
            System.out.println("DEBUG: finally block finished."); // DEBUG PRINT
        }
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
        // Để đảm bảo cập nhật UI ngay lập tức, đặc biệt nếu bạn đang chạy các tác vụ nặng
        // hoặc nếu có vấn đề về luồng.
        messageLabel.revalidate();
        messageLabel.repaint();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phone) {
        // Simple validation: 10 to 11 digits
        return phone.matches("^\\d{10,11}$");
    }
}