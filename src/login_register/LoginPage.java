package login_register;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import restaurantmanagement.DatabaseHelper;
import restaurantmanagement.Main;

public class LoginPage extends JPanel {

    private Main mainFrame; // Đảm bảo rằng mainFrame được truyền vào qua constructor

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;

    // Constructor mặc định dùng trong chương trình có sẵn frame
    public LoginPage(Main mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Nhà Hàng Gr8");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(46, 204, 113)); // MediumSeaGreen
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        add(loginButton, gbc);

        gbc.gridy = 4;
        registerButton = new JButton("Đăng ký");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(41, 128, 185)); // SteelBlue
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(150, 40));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Thay đổi duy nhất ở đây: gọi showPanel của mainFrame để chuyển sang trang "Register"
                if (mainFrame != null) {
                    mainFrame.showPanel("Register");
                } else {
                    // Xử lý trường hợp mainFrame là null (chỉ xảy ra khi test riêng lẻ LoginPage)
                    JOptionPane.showMessageDialog(LoginPage.this, "Không thể chuyển đến trang đăng ký. mainFrame không được khởi tạo.");
                }
            }
        });
        add(registerButton, gbc);

        gbc.gridy = 5;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng điền đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.getConnection();
            String sql = "SELECT password, role FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");

                if (password.equals(storedPassword)) {
                    LoginSession.setLoggedInUser(username, role);
                    messageLabel.setText("Đăng nhập thành công!");
                    messageLabel.setForeground(new Color(34, 139, 34));
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công với vai trò: " + role, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    if (mainFrame != null) {
                        mainFrame.showPanel("Home"); // Chuyển đến trang chủ sau khi đăng nhập thành công
                    } else {
                        System.out.println("mainFrame is null — không thể chuyển trang. " +
                                           "Đảm bảo LoginPage được khởi tạo với một instance Main hợp lệ.");
                        JOptionPane.showMessageDialog(this, "Đăng nhập thành công nhưng không thể chuyển trang. " +
                                "Vui lòng khởi động lại ứng dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    messageLabel.setText("Sai mật khẩu.");
                    messageLabel.setForeground(Color.RED);
                }
            } else {
                messageLabel.setText("Tên đăng nhập không tồn tại.");
                messageLabel.setForeground(Color.RED);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("Lỗi CSDL: " + ex.getMessage());
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame testFrame = new JFrame("Test Đăng nhập");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setSize(500, 400);
            testFrame.setLocationRelativeTo(null);

            // Khi test riêng lẻ, mainFrame là null. Trong ứng dụng thực tế, nó sẽ là một instance của Main.
            LoginPage testLoginPage = new LoginPage(null);
            testFrame.setContentPane(testLoginPage);
            testFrame.setVisible(true);
        });
    }
}