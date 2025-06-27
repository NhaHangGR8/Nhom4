package admincontroller;

import restaurantmanagement.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.text.SimpleDateFormat; // Import for date formatting

public class PaymentManagementPage extends JFrame {

    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JLabel totalRevenueLabel;

    private TableListPage tableListPageInstance;

    public PaymentManagementPage(TableListPage tableListPageInstance) {
        this.tableListPageInstance = tableListPageInstance;
        setTitle("Quản lý Thanh toán");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadUnpaidReservations();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Xác nhận Thanh toán cho Đặt bàn", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

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

        // New button for printing invoice
        JButton printInvoiceButton = new JButton("In Hóa đơn");
        printInvoiceButton.setFont(new Font("Arial", Font.BOLD, 16));
        printInvoiceButton.setBackground(new Color(52, 152, 219)); // A nice blue color
        printInvoiceButton.setForeground(Color.WHITE);
        printInvoiceButton.addActionListener(e -> printSelectedInvoice());

        totalRevenueLabel = new JLabel("Tổng số tiền: 0 VNĐ");
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalRevenueLabel.setForeground(new Color(39, 174, 96));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Panel for buttons
        buttonPanel.add(printInvoiceButton); // Add print button
        buttonPanel.add(confirmPaymentButton); // Add confirm button

        bottomPanel.add(totalRevenueLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST); // Add button panel to the east

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadUnpaidReservations() {
        tableModel.setRowCount(0);
        double totalRevenue = 0;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM reservations WHERE payment_status = 'unpaid' AND status = 'confirmed' ORDER BY reservation_date DESC")) {

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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận khách đã thanh toán cho đặt bàn ID " + reservationId + "?",
                "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                conn.setAutoCommit(false);

                String updateReservationSql = "UPDATE reservations SET payment_status = 'paid' WHERE id = ?";
                PreparedStatement stmtReservation = conn.prepareStatement(updateReservationSql);
                stmtReservation.setInt(1, reservationId);
                int rowsAffectedReservation = stmtReservation.executeUpdate();
                stmtReservation.close();

                if (rowsAffectedReservation > 0) {
                    String updateTableSql = "UPDATE tables SET status = 'available' WHERE id = ?";
                    PreparedStatement stmtTable = conn.prepareStatement(updateTableSql);
                    stmtTable.setInt(1, tableId);
                    int rowsAffectedTable = stmtTable.executeUpdate();
                    stmtTable.close();

                    if (rowsAffectedTable > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(this, "Xác nhận thanh toán và cập nhật trạng thái bàn thành công.");
                        loadUnpaidReservations();

                        if (tableListPageInstance != null) {
                            tableListPageInstance.refreshTableStatus();
                        }
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái thanh toán.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException e) {
                    // ignore
                }
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL khi cập nhật thanh toán hoặc trạng thái bàn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (conn != null) conn.setAutoCommit(true);
                    DatabaseHelper.closeConnection(conn);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    private void printSelectedInvoice() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt bàn để in hóa đơn.");
            return;
        }

        // Gather reservation details from the selected row
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);
        String customerEmail = (String) tableModel.getValueAt(selectedRow, 2);
        String customerPhone = (String) tableModel.getValueAt(selectedRow, 3);
        java.sql.Date reservationDate = (java.sql.Date) tableModel.getValueAt(selectedRow, 4);
        java.sql.Time reservationTime = (java.sql.Time) tableModel.getValueAt(selectedRow, 5);
        int tableId = (int) tableModel.getValueAt(selectedRow, 6);
        int numberOfGuests = (int) tableModel.getValueAt(selectedRow, 7);
        String totalPrice = (String) tableModel.getValueAt(selectedRow, 8); // Already formatted string

        // Format date and time for display
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(reservationDate);
        String formattedTime = timeFormat.format(reservationTime);

        // Build the invoice content
        StringBuilder invoiceContent = new StringBuilder();
        invoiceContent.append("----- HÓA ĐƠN THANH TOÁN -----\n");
        invoiceContent.append("------------------------------\n");
        invoiceContent.append(String.format("Mã đặt bàn: %d\n", id));
        invoiceContent.append(String.format("Khách hàng: %s\n", customerName));
        invoiceContent.append(String.format("Email: %s\n", customerEmail));
        invoiceContent.append(String.format("Số điện thoại: %s\n", customerPhone));
        invoiceContent.append(String.format("Ngày đặt: %s\n", formattedDate));
        invoiceContent.append(String.format("Giờ đặt: %s\n", formattedTime));
        invoiceContent.append(String.format("Bàn số: %d\n", tableId));
        invoiceContent.append(String.format("Số khách: %d\n", numberOfGuests));
        invoiceContent.append("------------------------------\n");
        invoiceContent.append(String.format("TỔNG CỘNG: %s\n", totalPrice));
        invoiceContent.append("------------------------------\n");
        invoiceContent.append("Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!\n");

        // Display the invoice in a dialog
        JTextArea invoiceArea = new JTextArea(invoiceContent.toString());
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        invoiceArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(invoiceArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Hóa đơn thanh toán", JOptionPane.PLAIN_MESSAGE);
    }
}