package admincontroller;

import restaurantmanagement.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Vector;

public class AdminTableCancellationPage extends JFrame {

    private JTextField cancelEmailField;
    private JTextField cancelPhoneField;
    private JTextField cancelDateField;
    private JLabel cancelStatusLabel;

    // New field for canceling by Table ID
    private JTextField tableIdField;
    private JLabel tableIdCancelStatusLabel; // New status label for table ID cancellation

    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;

    public AdminTableCancellationPage(TableListPage tableListPageInstance) {
        setTitle("Quản lý Hủy Đặt Bàn (Admin)");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setupUI();
        loadReservationsIntoTable(); // Load data initially
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        JLabel headerLabel = new JLabel("Hủy Đặt Bàn (Dành cho Quản trị viên)", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(178, 34, 34)); // Firebrick
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // --- Cancellation Form Panel (Customer Details) ---
        JPanel customerCancelFormPanel = new JPanel(new GridBagLayout());
        customerCancelFormPanel.setBackground(new Color(255, 255, 255));
        customerCancelFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(170, 170, 170)), "Hủy Đặt Bàn (Theo Thông tin Khách hàng)",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18), new Color(178, 34, 34)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; customerCancelFormPanel.add(new JLabel("Email đã đặt:"), gbc);
        gbc.gridx = 1; cancelEmailField = new JTextField(20); customerCancelFormPanel.add(cancelEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; customerCancelFormPanel.add(new JLabel("Số điện thoại đã đặt:"), gbc);
        gbc.gridx = 1; cancelPhoneField = new JTextField(20); customerCancelFormPanel.add(cancelPhoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; customerCancelFormPanel.add(new JLabel("Ngày đặt bàn (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; cancelDateField = new JTextField(15);
        cancelDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        customerCancelFormPanel.add(cancelDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton cancelButton = new JButton("Hủy Đặt Bàn Này");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 18));
        cancelButton.setBackground(new Color(231, 76, 60)); // Red
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        cancelButton.addActionListener(e -> confirmCancelReservation());
        customerCancelFormPanel.add(cancelButton, gbc);

        gbc.gridy = 4;
        cancelStatusLabel = new JLabel("", SwingConstants.CENTER);
        cancelStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        customerCancelFormPanel.add(cancelStatusLabel, gbc);

        // --- Cancellation Form Panel (Table ID) ---
        JPanel tableIdCancelFormPanel = new JPanel(new GridBagLayout());
        tableIdCancelFormPanel.setBackground(new Color(255, 255, 255));
        tableIdCancelFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(170, 170, 170)), "Hủy Đặt Bàn (Theo ID Bàn)",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18), new Color(41, 128, 185))); // Blue for this section

        GridBagConstraints gbcTableId = new GridBagConstraints();
        gbcTableId.insets = new Insets(8, 8, 8, 8);
        gbcTableId.fill = GridBagConstraints.HORIZONTAL;

        gbcTableId.gridx = 0; gbcTableId.gridy = 0; tableIdCancelFormPanel.add(new JLabel("ID Bàn:"), gbcTableId);
        gbcTableId.gridx = 1; tableIdField = new JTextField(10); tableIdCancelFormPanel.add(tableIdField, gbcTableId);

        gbcTableId.gridx = 0; gbcTableId.gridy = 1; gbcTableId.gridwidth = 2;
        JButton cancelByTableIdButton = new JButton("Hủy Đặt Bàn theo ID Bàn");
        cancelByTableIdButton.setFont(new Font("Arial", Font.BOLD, 18));
        cancelByTableIdButton.setBackground(new Color(52, 152, 219)); // Peter River
        cancelByTableIdButton.setForeground(Color.WHITE);
        cancelByTableIdButton.setFocusPainted(false);
        cancelByTableIdButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        cancelByTableIdButton.addActionListener(e -> cancelReservationByTableId());
        tableIdCancelFormPanel.add(cancelByTableIdButton, gbcTableId);

        gbcTableId.gridy = 2;
        tableIdCancelStatusLabel = new JLabel("", SwingConstants.CENTER);
        tableIdCancelStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tableIdCancelFormPanel.add(tableIdCancelStatusLabel, gbcTableId);


        // Combine both cancellation forms
        JPanel topFormPanel = new JPanel(new GridLayout(1, 2, 15, 0)); // 1 row, 2 columns, 15px horizontal gap
        topFormPanel.add(customerCancelFormPanel);
        topFormPanel.add(tableIdCancelFormPanel);
        topFormPanel.setBackground(mainPanel.getBackground());
        mainPanel.add(topFormPanel, BorderLayout.CENTER);


        // --- Reservations List Table ---
        JPanel tableListPanel = new JPanel(new BorderLayout(5, 5));
        tableListPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(170, 170, 170)), "Danh Sách Đặt Bàn Hiện Có",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18), new Color(41, 128, 185)));
        tableListPanel.setBackground(new Color(255, 255, 255));

        String[] columnNames = {"ID Đặt Bàn", "Tên Khách Hàng", "Email", "SĐT", "Ngày Đặt", "Giờ Đặt", "Số Khách", "Bàn ID", "Trạng Thái", "Yêu Cầu Đặc Biệt"};
        reservationsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reservationsTable.setRowHeight(25);
        reservationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        reservationsTable.getTableHeader().setBackground(new Color(173, 216, 230)); // Light Blue
        reservationsTable.getTableHeader().setForeground(Color.BLACK);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170), 1));
        tableListPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Làm mới danh sách");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.setBackground(new Color(52, 152, 219)); // Peter River
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadReservationsIntoTable());
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBackground(tableListPanel.getBackground());
        refreshPanel.add(refreshButton);
        tableListPanel.add(refreshPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topFormPanel, tableListPanel);
        splitPane.setResizeWeight(0.3); // Give more space to the table list
        splitPane.setDividerLocation(300); // Adjusted initial divider location to accommodate two forms
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void confirmCancelReservation() {
        String email = cancelEmailField.getText().trim();
        String phone = cancelPhoneField.getText().trim();
        String dateText = cancelDateField.getText().trim();

        if (email.isEmpty() || phone.isEmpty() || dateText.isEmpty()) {
            cancelStatusLabel.setText("Vui lòng điền đầy đủ thông tin.");
            cancelStatusLabel.setForeground(Color.RED);
            return;
        }

        LocalDate reservationDate;
        try {
            reservationDate = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ex) {
            cancelStatusLabel.setText("Ngày đặt bàn không hợp lệ. Định dạng: dd/MM/yyyy");
            cancelStatusLabel.setForeground(Color.RED);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy đặt bàn này?", "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            cancelReservation(email, phone, reservationDate);
        }
    }

    private void cancelReservation(String email, String phone, LocalDate date) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseHelper.getConnection();
            String sql = "UPDATE reservations SET status = 'cancelled' WHERE customer_email = ? AND customer_phone = ? AND reservation_date = ? AND status = 'confirmed'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setDate(3, java.sql.Date.valueOf(date));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                cancelStatusLabel.setText("Hủy đặt bàn thành công!");
                cancelStatusLabel.setForeground(new Color(34, 139, 34)); // Forest Green
                JOptionPane.showMessageDialog(this, "Đặt bàn đã được hủy thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadReservationsIntoTable(); // Refresh the table
            } else {
                cancelStatusLabel.setText("Không tìm thấy đặt bàn hoặc đã hủy.");
                cancelStatusLabel.setForeground(Color.ORANGE);
                JOptionPane.showMessageDialog(this, "Không tìm thấy đặt bàn phù hợp để hủy hoặc đặt bàn đã được hủy trước đó.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            cancelStatusLabel.setText("Lỗi CSDL khi hủy: " + ex.getMessage());
            cancelStatusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // New method to cancel reservation by table ID
    private void cancelReservationByTableId() {
        int tableId;
        try {
            tableId = Integer.parseInt(tableIdField.getText().trim());
        } catch (NumberFormatException e) {
            tableIdCancelStatusLabel.setText("ID Bàn không hợp lệ.");
            tableIdCancelStatusLabel.setForeground(Color.RED);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy đặt bàn cho Bàn ID: " + tableId + "?", "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();

                // Kiểm tra xem có đặt bàn active nào không
                String checkSql = "SELECT id FROM reservations WHERE table_id = ? AND status = 'confirmed'"; // Changed 'active' to 'confirmed' for consistency
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, tableId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    tableIdCancelStatusLabel.setText("Không có đặt bàn nào đang hoạt động cho bàn này.");
                    tableIdCancelStatusLabel.setForeground(Color.ORANGE);
                    JOptionPane.showMessageDialog(this, "Không có đặt bàn nào đang hoạt động cho bàn này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int reservationId = rs.getInt("id");
                rs.close();
                checkStmt.close();

                // Cập nhật trạng thái của đặt bàn
                String cancelSql = "UPDATE reservations SET status = 'cancelled' WHERE id = ?";
                PreparedStatement cancelStmt = conn.prepareStatement(cancelSql);
                cancelStmt.setInt(1, reservationId);
                cancelStmt.executeUpdate();
                cancelStmt.close();

                // Cập nhật trạng thái bàn
                String updateTableSql = "UPDATE tables SET status = 'available' WHERE id = ?";
                PreparedStatement updateTableStmt = conn.prepareStatement(updateTableSql);
                updateTableStmt.setInt(1, tableId);
                updateTableStmt.executeUpdate();
                updateTableStmt.close();

                tableIdCancelStatusLabel.setText("Hủy đặt bàn thành công cho Bàn ID: " + tableId);
                tableIdCancelStatusLabel.setForeground(new Color(34, 139, 34)); // Forest Green
                JOptionPane.showMessageDialog(this, "Hủy đặt bàn thành công cho Bàn ID: " + tableId + ".", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadReservationsIntoTable(); // Refresh the table
            } catch (SQLException ex) {
                ex.printStackTrace();
                tableIdCancelStatusLabel.setText("Lỗi khi hủy đặt bàn: " + ex.getMessage());
                tableIdCancelStatusLabel.setForeground(Color.RED);
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy đặt bàn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }
    }

    private void loadReservationsIntoTable() {
        reservationsTableModel.setRowCount(0); // Clear existing data
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            String sql = "SELECT id, customer_name, customer_email, customer_phone, reservation_date, reservation_time, number_of_guests, table_id, status, special_requests FROM reservations ORDER BY reservation_date DESC, reservation_time DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getString("customer_email"));
                row.add(rs.getString("customer_phone"));
                row.add(rs.getDate("reservation_date").toLocalDate());
                row.add(rs.getTime("reservation_time").toLocalTime());
                row.add(rs.getInt("number_of_guests"));
                row.add(rs.getInt("table_id"));
                row.add(rs.getString("status"));
                row.add(rs.getString("special_requests"));
                reservationsTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách đặt bàn: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }
}