package admincontroller;

import restaurantmanagement.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class TableListPage extends JFrame {

    private JTable tableStatusTable;
    private DefaultTableModel tableModel; 
    private Object guests;

    public TableListPage() {
        setTitle("Danh Sách Bàn (Admin)");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setupUI();
        loadTableData(); // Call the new method to load data initially
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 255, 240)); // Honeydew

        JLabel headerLabel = new JLabel("Trạng Thái Các Bàn Trong Nhà Hàng", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(34, 139, 34)); // Forest Green
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID Bàn", "Vị trí", "Trạng thái", "Số khách hiện tại"}; 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are not editable
            }
        };
        tableStatusTable = new JTable(tableModel); // Use tableModel
        tableStatusTable.setFont(new Font("Arial", Font.PLAIN, 14));
        tableStatusTable.setRowHeight(25);
        tableStatusTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tableStatusTable.getTableHeader().setBackground(new Color(144, 238, 144)); // Light Green
        tableStatusTable.getTableHeader().setForeground(Color.BLACK);
        tableStatusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableStatusTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Làm mới trạng thái bàn");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadTableData()); // Call loadTableData on refresh
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(mainPanel.getBackground());
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    // Changed method visibility from private to public
    public void loadTableData() { 
        tableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = """
                SELECT t.id, t.location, t.status,
                    (
                        SELECT r.number_of_guests
                        FROM reservations r
                        WHERE r.table_id = t.id AND r.status = 'active'
                        ORDER BY r.created_at DESC
                        LIMIT 1
                    ) AS number_of_guests
                FROM tables t
                ORDER BY t.id
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("location"));
                row.add(rs.getString("status"));
                Object guestInfo = rs.wasNull() ? "—" : guests;
                row.add(guestInfo);
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu bàn: " + ex.getMessage()); 
        }
    }

    public void refreshTableStatus() {
        loadTableData();
    }
}