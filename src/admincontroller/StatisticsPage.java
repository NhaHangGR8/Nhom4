package admincontroller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import restaurantmanagement.DatabaseHelper;

public class StatisticsPage extends JFrame {
    private JTable reservationStatsTable;
    private JTable revenueTable;

    public StatisticsPage() {
        setTitle("Thống kê đặt bàn và doanh thu");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));

        // Khởi tạo bảng
        reservationStatsTable = new JTable();
        revenueTable = new JTable();

        // Định dạng bảng đặt bàn
        reservationStatsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reservationStatsTable.setRowHeight(25);
        reservationStatsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        reservationStatsTable.getTableHeader().setBackground(new Color(173, 216, 230));

        // Định dạng bảng doanh thu
        revenueTable.setFont(new Font("Arial", Font.PLAIN, 14));
        revenueTable.setRowHeight(25);
        revenueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        revenueTable.getTableHeader().setBackground(new Color(173, 216, 230));

        // Thêm vào giao diện
        add(new JScrollPane(reservationStatsTable));
        add(new JScrollPane(revenueTable));

        // Tải dữ liệu
        loadReservationStats();
        loadRevenueStats();
    }

    private void loadReservationStats() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tháng");
        model.addColumn("Số bàn đã thanh toán");
        model.addColumn("Số bàn đã hủy");

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT DATE_FORMAT(reservation_date, '%Y-%m') AS month, " +
                         "SUM(CASE WHEN payment_status = 'paid' THEN 1 ELSE 0 END) AS paid_count, " +
                         "SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END) AS cancelled_count " +
                         "FROM reservations " +
                         "GROUP BY month ORDER BY month DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("month"));
                row.add(rs.getString("paid_count"));
                row.add(rs.getString("cancelled_count"));
                model.addRow(row);
            }

            reservationStatsTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải thống kê đặt bàn: " + e.getMessage());
        }
    }

    private void loadRevenueStats() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tháng");
        model.addColumn("Tổng doanh thu (VNĐ)");

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT DATE_FORMAT(reservation_date, '%Y-%m') AS month, " +
                         "SUM(total_price) AS revenue " +
                         "FROM reservations " +
                         "WHERE payment_status = 'paid' " +
                         "GROUP BY month ORDER BY month DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("month"));
                row.add(String.format("%,.0f", rs.getDouble("revenue")));
                model.addRow(row);
            }

            revenueTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải thống kê doanh thu: " + e.getMessage());
        }
    }
}
