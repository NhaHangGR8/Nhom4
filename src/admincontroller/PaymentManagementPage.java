package admincontroller;

import restaurantmanagement.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class PaymentManagementPage extends JFrame {

    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JLabel totalRevenueLabel; // Thêm label hiển thị tổng tiền

    // Add a reference to TableListPage
    private TableListPage tableListPageInstance; 

    public PaymentManagementPage(TableListPage tableListPageInstance) { // Constructor now accepts TableListPage
        this.tableListPageInstance = tableListPageInstance; // Store the instance
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

        totalRevenueLabel = new JLabel("Tổng số tiền: 0 VNĐ"); // Khởi tạo label
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalRevenueLabel.setForeground(new Color(39, 174, 96));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Bên trái: tổng tiền, bên phải: nút xác nhận
        bottomPanel.add(totalRevenueLabel, BorderLayout.WEST);
        bottomPanel.add(confirmPaymentButton, BorderLayout.EAST);

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
        int tableId = (int) tableModel.getValueAt(selectedRow, 6); // Get table_id from the selected row

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận khách đã thanh toán cho đặt bàn ID " + reservationId + "?",
                "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                conn.setAutoCommit(false); // Start transaction

                // 1. Update reservation status to 'paid'
                String updateReservationSql = "UPDATE reservations SET payment_status = 'paid' WHERE id = ?";
                PreparedStatement stmtReservation = conn.prepareStatement(updateReservationSql);
                stmtReservation.setInt(1, reservationId);
                int rowsAffectedReservation = stmtReservation.executeUpdate();
                stmtReservation.close();

                if (rowsAffectedReservation > 0) {
                    // 2. Update table status to 'available'
                    String updateTableSql = "UPDATE tables SET status = 'available' WHERE id = ?";
                    PreparedStatement stmtTable = conn.prepareStatement(updateTableSql);
                    stmtTable.setInt(1, tableId);
                    int rowsAffectedTable = stmtTable.executeUpdate();
                    stmtTable.close();

                    if (rowsAffectedTable > 0) {
                        conn.commit(); // Commit transaction
                        JOptionPane.showMessageDialog(this, "Xác nhận thanh toán và cập nhật trạng thái bàn thành công.");
                        loadUnpaidReservations(); // Refresh table và tổng tiền

                        // Refresh the TableListPage
                        if (tableListPageInstance != null) {
                            tableListPageInstance.refreshTableStatus(); 
                        }
                    } else {
                        conn.rollback(); // Rollback if table update fails
                        JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái bàn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    conn.rollback(); // Rollback if reservation update fails
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái thanh toán.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                try {
                    if (conn != null) conn.rollback(); // Rollback on error
                } catch (SQLException e) {
                    // ignore
                }
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL khi cập nhật thanh toán hoặc trạng thái bàn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (conn != null) conn.setAutoCommit(true); // Reset auto-commit
                    DatabaseHelper.closeConnection(conn);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}