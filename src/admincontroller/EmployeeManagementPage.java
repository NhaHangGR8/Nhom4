// admincontroller/EmployeeManagementPage.java
package admincontroller;

import restaurantmanagement.DatabaseHelper; // Đảm bảo đúng package của DatabaseHelper

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import login_register.LoginSession;

public class EmployeeManagementPage extends JFrame {

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JComboBox<String> roleFilterComboBox;

    public EmployeeManagementPage() {
        setTitle("Quản lý Nhân viên (Người dùng)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadEmployees();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Quản lý Thông tin Nhân viên (Người dùng)", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Search and Filter Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        topPanel.add(new JLabel("Tìm kiếm (Username/Email/SĐT):"));
        searchField = new JTextField(20);
        topPanel.add(searchField);

        topPanel.add(new JLabel("Lọc theo Vai trò:"));
        roleFilterComboBox = new JComboBox<>(new String[]{"Tất cả", "admin", "user"});
        topPanel.add(roleFilterComboBox);
        roleFilterComboBox.addActionListener(e -> loadEmployees());

        searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> loadEmployees());
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Table
        // Đã cập nhật tên cột từ "Số điện thoại" thành "Số điện thoại" (không đổi string hiển thị)
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Username", "Email", "Số điện thoại", "Vai trò"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        addButton = new JButton("Thêm mới");
        editButton = new JButton("Chỉnh sửa");
        deleteButton = new JButton("Xóa");

        addButton.addActionListener(e -> openAddEditEmployeeDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) tableModel.getValueAt(selectedRow, 0);
                openAddEditEmployeeDialog(userId);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để chỉnh sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> deleteEmployee());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        String searchTerm = searchField.getText().trim();
        String roleFilter = (String) roleFilterComboBox.getSelectedItem();

        // Đã cập nhật tên cột trong truy vấn SQL
        StringBuilder sql = new StringBuilder("SELECT id, username, email, phone_number, role FROM users WHERE 1=1");

        if (!searchTerm.isEmpty()) {
            // Đã cập nhật tên cột trong truy vấn SQL
            sql.append(" AND (username LIKE ? OR email LIKE ? OR phone_number LIKE ?)");
        }
        if (!"Tất cả".equals(roleFilter)) {
            sql.append(" AND role = ?");
        }
        sql.append(" ORDER BY id ASC");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (!searchTerm.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchTerm + "%");
                pstmt.setString(paramIndex++, "%" + searchTerm + "%");
                pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            }
            if (!"Tất cả".equals(roleFilter)) {
                pstmt.setString(paramIndex++, roleFilter);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("email"));
                // Đã cập nhật tên cột lấy từ ResultSet
                row.add(rs.getString("phone_number"));
                row.add(rs.getString("role"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddEditEmployeeDialog(Integer userId) {
        JDialog dialog = new JDialog(this, userId == null ? "Thêm người dùng mới" : "Chỉnh sửa người dùng", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneNumberField = new JTextField(20); // Đã đổi tên biến
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"user", "admin"});

        // Labels and fields
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialog.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialog.add(confirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; dialog.add(emailField, gbc);

        // Đã cập nhật label và biến cho số điện thoại
        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; dialog.add(phoneNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; dialog.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; dialog.add(roleComboBox, gbc);

        // Load existing data if editing
        if (userId != null) {
            // Đã cập nhật tên cột trong truy vấn SQL
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT username, email, phone_number, role FROM users WHERE id = ?")) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    usernameField.setText(rs.getString("username"));
                    emailField.setText(rs.getString("email"));
                    // Đã cập nhật tên cột lấy từ ResultSet
                    phoneNumberField.setText(rs.getString("phone_number"));
                    roleComboBox.setSelectedItem(rs.getString("role"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tải dữ liệu người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            usernameField.setEditable(false);
        }

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = emailField.getText().trim();
            String phoneNumber = phoneNumberField.getText().trim(); // Đã đổi tên biến
            String role = (String) roleComboBox.getSelectedItem();

            // Đã cập nhật biến phone thành phoneNumber
            if (username.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || role == null) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ các trường (trừ mật khẩu khi chỉnh sửa nếu không muốn đổi).", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (userId == null) { // Add new user
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mật khẩu cho người dùng mới.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Mật khẩu xác nhận không khớp.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Đã cập nhật tham số phương thức
                addUser(username, password, email, phoneNumber, role, dialog);
            } else { // Edit existing user
                // Đã cập nhật tham số phương thức
                updateUser(userId, username, password, confirmPassword, email, phoneNumber, role, dialog);
            }
        });

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(saveButton, gbc);

        dialog.setVisible(true);
    }

    // Đã cập nhật tham số phương thức
    private void addUser(String username, String password, String email, String phoneNumber, String role, JDialog dialog) {
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            // Đã cập nhật tên cột trong truy vấn SQL
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ? OR phone_number = ?";
            PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setString(1, username);
            checkPstmt.setString(2, email);
            checkPstmt.setString(3, phoneNumber); // Đã cập nhật tham số
            ResultSet rs = checkPstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(dialog, "Username, Email hoặc Số điện thoại đã tồn tại.", "Lỗi trùng lặp", JOptionPane.WARNING_MESSAGE);
                return;
            }
            checkPstmt.close();
            rs.close();

            // Đã cập nhật tên cột trong truy vấn SQL
            String insertSql = "INSERT INTO users (username, password, email, phone_number, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber); // Đã cập nhật tham số
            pstmt.setString(5, role);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(dialog, "Thêm người dùng thành công!");
            dialog.dispose();
            loadEmployees();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi: Username, Email hoặc Số điện thoại đã tồn tại. Vui lòng nhập thông tin khác.", "Lỗi trùng lặp", JOptionPane.WARNING_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Lỗi CSDL khi thêm người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
        }
    }

    // Đã cập nhật tham số phương thức
    private void updateUser(int userId, String username, String newPassword, String confirmNewPassword, String email, String phoneNumber, String role, JDialog dialog) {
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();

            // Đã cập nhật tên cột trong truy vấn SQL
            String checkSql = "SELECT COUNT(*) FROM users WHERE (email = ? OR phone_number = ?) AND id <> ?";
            PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setString(1, email);
            checkPstmt.setString(2, phoneNumber); // Đã cập nhật tham số
            checkPstmt.setInt(3, userId);
            ResultSet rs = checkPstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(dialog, "Email hoặc Số điện thoại đã tồn tại cho người dùng khác.", "Lỗi trùng lặp", JOptionPane.WARNING_MESSAGE);
                return;
            }
            checkPstmt.close();
            rs.close();

            // Đã cập nhật tên cột trong truy vấn SQL
            StringBuilder updateSql = new StringBuilder("UPDATE users SET email = ?, phone_number = ?, role = ?");
            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmNewPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Mật khẩu xác nhận không khớp.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                updateSql.append(", password = ?");
            }
            updateSql.append(" WHERE id = ?");

            PreparedStatement pstmt = conn.prepareStatement(updateSql.toString());
            int paramIndex = 1;
            pstmt.setString(paramIndex++, email);
            pstmt.setString(paramIndex++, phoneNumber); // Đã cập nhật tham số
            pstmt.setString(paramIndex++, role);
            if (!newPassword.isEmpty()) {
                pstmt.setString(paramIndex++, newPassword);
            }
            pstmt.setInt(paramIndex++, userId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(dialog, "Cập nhật người dùng thành công!");
            dialog.dispose();
            loadEmployees();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi: Email hoặc Số điện thoại đã tồn tại. Vui lòng nhập thông tin khác.", "Lỗi trùng lặp", JOptionPane.WARNING_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Lỗi CSDL khi cập nhật người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
        }
    }


    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String role = (String) tableModel.getValueAt(selectedRow, 4);

        if (role.equalsIgnoreCase("admin") && LoginSession.getCurrentUsername() != null && LoginSession.getCurrentUsername().equals(username)) {
            JOptionPane.showMessageDialog(this, "Bạn không thể xóa tài khoản admin của chính mình.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa người dùng '" + username + "' (ID: " + userId + ")?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                String sql = "DELETE FROM users WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!");
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                if (ex instanceof SQLIntegrityConstraintViolationException) {
                    JOptionPane.showMessageDialog(this, "Không thể xóa người dùng này vì có dữ liệu liên quan trong các bảng khác (ví dụ: đặt bàn).", "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
                } else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi CSDL khi xóa người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            login_register.LoginSession.setLoggedInUser("testadmin", "admin");
            new EmployeeManagementPage().setVisible(true);
        });
    }
}