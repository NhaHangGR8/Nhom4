// PaymentManagementPage.java
package admincontroller;

import restaurantmanagement.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class PaymentManagementPage extends JFrame {

    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JLabel totalRevenueLabel;
    private JTextField searchPhoneField;
    private JTextField searchEmailField;
    private JButton searchButton;

    private TableListPage tableListPageInstance;

    public PaymentManagementPage(TableListPage tableListPageInstance) {
        this.tableListPageInstance = tableListPageInstance;
        setTitle("Quản lý Thanh toán");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadUnpaidReservations(null, null);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Xác nhận Thanh toán cho Đặt bàn", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        searchPanel.add(new JLabel("Tìm kiếm SĐT:"));
        searchPhoneField = new JTextField(12);
        searchPanel.add(searchPhoneField);

        searchPanel.add(new JLabel("Tìm kiếm Email:"));
        searchEmailField = new JTextField(15);
        searchPanel.add(searchEmailField);

        searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> {
            String phoneNumber = searchPhoneField.getText().trim();
            String email = searchEmailField.getText().trim();
            loadUnpaidReservations(phoneNumber.isEmpty() ? null : phoneNumber, email.isEmpty() ? null : email);
        });
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
                "ID", "Tên KH", "Email", "SĐT", "Ngày", "Giờ", "Bàn", "Số khách", "Tổng tiền"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(tableModel);
        reservationTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton confirmPaymentButton = new JButton("Xác nhận đã Thanh toán");
        confirmPaymentButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmPaymentButton.setBackground(new Color(46, 204, 113));
        confirmPaymentButton.setForeground(Color.WHITE);
        confirmPaymentButton.addActionListener(e -> confirmSelectedPayment());

        JButton printInvoiceButton = new JButton("In Hóa đơn");
        printInvoiceButton.setFont(new Font("Arial", Font.BOLD, 16));
        printInvoiceButton.setBackground(new Color(52, 152, 219));
        printInvoiceButton.setForeground(Color.WHITE);
        printInvoiceButton.addActionListener(e -> printSelectedInvoice());

        totalRevenueLabel = new JLabel("Tổng số tiền: 0 VNĐ");
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalRevenueLabel.setForeground(new Color(39, 174, 96));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(printInvoiceButton);
        buttonPanel.add(confirmPaymentButton);

        bottomPanel.add(totalRevenueLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadUnpaidReservations(String phoneNumber, String email) {
        tableModel.setRowCount(0);
        double totalRevenue = 0;

        StringBuilder sql = new StringBuilder("SELECT * FROM reservations WHERE payment_status = 'unpaid' AND status = 'confirmed'");
        int paramIndex = 1;

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            sql.append(" AND customer_phone LIKE ?");
        }
        if (email != null && !email.isEmpty()) {
            sql.append(" AND customer_email LIKE ?");
        }
        sql.append(" ORDER BY reservation_date DESC");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                stmt.setString(paramIndex++, "%" + phoneNumber + "%");
            }
            if (email != null && !email.isEmpty()) {
                stmt.setString(paramIndex++, "%" + email + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getString("customer_email"));
                row.add(rs.getString("customer_phone"));
                row.add(rs.getDate("reservation_date"));
                row.add(rs.getTime("reservation_time"));
                row.add(rs.getInt("table_id"));
                row.add(rs.getInt("number_of_guests"));
                double price = rs.getDouble("total_price");
                row.add(String.format("%,.0f VNĐ", price));
                totalRevenue += price;
                tableModel.addRow(row);
            }
            totalRevenueLabel.setText("Tổng số tiền: " + String.format("%,.0f VNĐ", totalRevenue));

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đặt bàn chưa thanh toán.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmSelectedPayment() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt bàn để xác nhận thanh toán.");
            return;
        }

        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        int tableId = (int) tableModel.getValueAt(selectedRow, 6);

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận khách đã thanh toán cho đặt bàn ID " + reservationId + "?", "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                conn.setAutoCommit(false); // Bắt đầu transaction

                System.out.println("DEBUG: Confirming payment for Reservation ID: " + reservationId);
                System.out.println("DEBUG: Table ID to update: " + tableId);

                // Update reservation status
                String updateReservationSql = "UPDATE reservations SET payment_status = 'paid' WHERE id = ?";
                PreparedStatement stmtReservation = conn.prepareStatement(updateReservationSql);
                stmtReservation.setInt(1, reservationId);
                int rowsAffectedReservation = stmtReservation.executeUpdate();
                System.out.println("DEBUG: Rows affected for reservation update: " + rowsAffectedReservation);
                stmtReservation.close();

                if (rowsAffectedReservation > 0) {
                    // Update table status to 'available' if the reservation is paid
                    String updateTableSql = "UPDATE tables SET status = 'available' WHERE id = ?";
                    PreparedStatement stmtTable = conn.prepareStatement(updateTableSql);
                    stmtTable.setInt(1, tableId);
                    int rowsAffectedTable = stmtTable.executeUpdate();
                    System.out.println("DEBUG: Rows affected for table status update: " + rowsAffectedTable);
                    stmtTable.close();

                    if (rowsAffectedTable > 0) {
                        conn.commit(); // Commit transaction if both updates are successful
                        JOptionPane.showMessageDialog(this, "Thanh toán thành công và trạng thái bàn đã được cập nhật!");
                        loadUnpaidReservations(searchPhoneField.getText().trim(), searchEmailField.getText().trim()); // Reload table data

                        // NEW: Refresh table status in TableListPage
                        if (tableListPageInstance != null) {
                            tableListPageInstance.refreshTableStatus();
                            System.out.println("DEBUG: refreshTableStatus called from PaymentManagementPage.");
                        }

                    } else {
                        conn.rollback(); // Rollback if table update fails
                        JOptionPane.showMessageDialog(this, "Lỗi: Không thể cập nhật trạng thái bàn. Giao tác đã được hoàn tác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        System.err.println("ERROR: Table status update failed for table ID: " + tableId);
                    }
                } else {
                    conn.rollback(); // Rollback if reservation update fails
                    JOptionPane.showMessageDialog(this, "Lỗi: Không thể cập nhật trạng thái đặt bàn. Giao tác đã được hoàn tác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    System.err.println("ERROR: Reservation status update failed for reservation ID: " + reservationId);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException rbEx) {
                    rbEx.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Lỗi CSDL khi xác nhận thanh toán: " + ex.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                System.err.println("ERROR: SQLException in confirmSelectedPayment(): " + ex.getMessage());
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }
    }

    private void printSelectedInvoice() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt bàn để in hóa đơn.");
            return;
        }

        // Retrieve data from the selected row
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);
        String customerEmail = (String) tableModel.getValueAt(selectedRow, 2);
        // Assuming date and time are java.sql.Date and java.sql.Time
        java.sql.Date reservationDate = (java.sql.Date) tableModel.getValueAt(selectedRow, 4);
        java.sql.Time reservationTime = (java.sql.Time) tableModel.getValueAt(selectedRow, 5);
        int tableId = (int) tableModel.getValueAt(selectedRow, 6);
        String totalPrice = (String) tableModel.getValueAt(selectedRow, 8); // Already formatted as string

        // Format date and time for invoice
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(reservationDate);
        String formattedTime = timeFormat.format(reservationTime);

        StringBuilder invoiceContent = new StringBuilder();
        invoiceContent.append("----- HÓA ĐƠN THANH TOÁN -----\n");
        invoiceContent.append("------------------------------\n");
        invoiceContent.append(String.format("Mã đặt bàn: %d\n", id));
        invoiceContent.append(String.format("Khách hàng: %s\n", customerName));
        invoiceContent.append(String.format("Email: %s\n", customerEmail));
        invoiceContent.append(String.format("Hóa đơn được xuất vào ngày: %s\n", formattedDate));
        invoiceContent.append(String.format("Giờ: %s\n", formattedTime));
        invoiceContent.append(String.format("Bàn số: %d\n", tableId));
        invoiceContent.append("------------------------------\n");
        invoiceContent.append(String.format("TỔNG CỘNG: %s\n", totalPrice));
        invoiceContent.append("------------------------------\n");
        invoiceContent.append("Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!\n");

        JTextArea invoiceArea = new JTextArea(invoiceContent.toString());
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        invoiceArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(invoiceArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Hóa đơn thanh toán", JOptionPane.PLAIN_MESSAGE);
    }
}