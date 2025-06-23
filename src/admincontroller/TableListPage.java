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
        headerLabel.setForeground(new Color(0, 102, 0)); // Dark Green
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID Bàn", "Vị Trí", "Trạng Thái", "Số Khách"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        tableStatusTable = new JTable(tableModel);
        tableStatusTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableStatusTable.setRowHeight(25);
        tableStatusTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableStatusTable.getTableHeader().setBackground(new Color(173, 216, 230)); // Light Blue
        tableStatusTable.getTableHeader().setForeground(Color.BLACK);
        tableStatusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection

        JScrollPane scrollPane = new JScrollPane(tableStatusTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadTableData() {
        tableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = """
                SELECT t.id, t.location, t.status,
                    (
                        SELECT r.number_of_guests
                        FROM reservations r
                        WHERE r.table_id = t.id AND r.status IN ('active', 'confirmed', 'pending') -- Thay đổi ở đây
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
                String tableStatus = rs.getString("status");
                row.add(tableStatus);

                Object guestInfo;
                // Hiển thị số khách nếu bàn ở trạng thái 'occupied' hoặc 'reserved'
                if ("occupied".equalsIgnoreCase(tableStatus) || "reserved".equalsIgnoreCase(tableStatus)) {
                    int numberOfGuests = rs.getInt("number_of_guests");
                    if (rs.wasNull()) {
                        guestInfo = "— (Không tìm thấy thông tin khách)"; // Có thể thông báo rõ hơn nếu không có
                    } else {
                        guestInfo = numberOfGuests;
                    }
                } else {
                    guestInfo = "—"; // Bàn trống hoặc trạng thái khác
                }
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