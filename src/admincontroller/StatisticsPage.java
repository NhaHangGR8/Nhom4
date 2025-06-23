package admincontroller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import restaurantmanagement.DatabaseHelper;

public class StatisticsPage extends JFrame {
    private JTable reservationTable;
    private JTable revenueTable;

    public StatisticsPage() {
        setTitle("Thống kê đặt bàn và doanh thu");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));

        reservationTable = new JTable();
        revenueTable = new JTable();

        add(new JScrollPane(reservationTable));
        add(new JScrollPane(revenueTable));

        loadReservationStats();
        loadRevenueStats();
    }

    private void loadReservationStats() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Ngày");
        model.addColumn("Số lượt đặt bàn");

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT reservation_date, COUNT(*) AS total_reservations " +
                         "FROM reservations GROUP BY reservation_date ORDER BY reservation_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("reservation_date"));
                row.add(String.valueOf(rs.getInt("total_reservations")));
                model.addRow(row);
            }

            reservationTable.setModel(model);
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
                         "FROM reservations GROUP BY month ORDER BY month DESC";
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
