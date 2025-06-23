package restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JPanel {

    private Main mainFrame;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;

    // Constructor cho trường hợp đăng nhập lại sau khi đăng xuất
    public LoginPage() {
        JFrame frame = new JFrame("Đăng nhập");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(this);
        this.mainFrame = null;
        setupUI();
        frame.setVisible(true);
    }

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

        JLabel titleLabel = new JLabel("Đăng nhập Hệ Thống Quản Lý Nhà Hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(messageLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(getBackground());

        loginButton = new JButton("Đăng Nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> authenticateUser());
        buttonPanel.add(loginButton);

        registerButton = new JButton("Đăng Ký");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(100, 149, 237));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> mainFrame.showPanel("Register"));
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng nhập tên đăng nhập và mật khẩu.");
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
                        mainFrame.showPanel("Home");
                    } else {
                        System.out.println("mainFrame is null — không thể chuyển trang.");
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
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { }
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
        }
    }
}
