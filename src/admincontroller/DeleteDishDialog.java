package admincontroller;

import restaurantmanagement.DatabaseHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class DeleteDishDialog extends JDialog {

    private JTable dishTable;
    private DefaultTableModel tableModel;
    private JButton deleteSelectedButton;
    private JButton cancelButton;
    private Map<String, String> categoryMap; // Để hiển thị tên danh mục tiếng Việt

    private boolean itemDeleted = false; // Flag to indicate if an item was deleted

    public DeleteDishDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        initializeCategoryMap();
        setupUI();
        loadDishesIntoTable(); // Load dishes when the dialog is opened
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setTitle("Xóa món ăn"); // Set dialog title
    }

    private void initializeCategoryMap() {
        categoryMap = new LinkedHashMap<>();
        categoryMap.put("Món chính", "main_course");
        categoryMap.put("Món khai vị", "appetizer");
        categoryMap.put("Tráng miệng", "dessert");
        categoryMap.put("Đồ uống", "beverage");
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        // SỬA LỖI: Áp dụng border cho contentPane thay vì trực tiếp cho JDialog
        

        String[] columnNames = {"ID", "Tên món", "Mô tả", "Danh mục", "Giá"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        dishTable = new JTable(tableModel);
        dishTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row to be selected
        dishTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering
        dishTable.setAutoCreateRowSorter(true); // Enable sorting

        JScrollPane tableScrollPane = new JScrollPane(dishTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteSelectedButton = new JButton("Xóa món đã chọn");
        cancelButton = new JButton("Hủy");
        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        deleteSelectedButton.addActionListener(e -> deleteSelectedDish());
        cancelButton.addActionListener(e -> {
            itemDeleted = false; // Ensure flag is false if canceled
            dispose();
        });
    }

    private void loadDishesIntoTable() {
        tableModel.setRowCount(0); // Clear existing data
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT id, name, description, category, price FROM dishes ORDER BY name"; // Order for better display
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String categoryEnglish = rs.getString("category");
                double price = rs.getDouble("price");

                String categoryVietnamese = getVietnameseCategoryName(categoryEnglish); // Convert to Vietnamese

                Vector<Object> row = new Vector<>();
                row.add(id);
                row.add(name);
                row.add(description);
                row.add(categoryVietnamese);
                row.add(String.format("%,.0f VNĐ", price)); // Format price for display
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách món ăn vào bảng: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu món ăn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void deleteSelectedDish() {
        int selectedRow = dishTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn từ bảng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa món ăn này không?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = dishTable.convertRowIndexToModel(selectedRow);
            int dishIdToDelete = (int) tableModel.getValueAt(modelRow, 0);

            Connection conn = null;
            PreparedStatement pstmt = null;
            try {
                conn = DatabaseHelper.getConnection();
                String sql = "DELETE FROM dishes WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, dishIdToDelete);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Xóa món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    itemDeleted = true; // Set flag to true
                    loadDishesIntoTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa món ăn. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    itemDeleted = false;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL khi xóa món ăn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                itemDeleted = false;
            } finally {
                DatabaseHelper.closeConnection(conn);
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    public boolean isItemDeleted() {
        return itemDeleted;
    }

    // Helper to get Vietnamese category name from English
    private String getVietnameseCategoryName(String englishCategory) {
        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            if (entry.getValue().equals(englishCategory)) {
                return entry.getKey();
            }
        }
        return englishCategory; // Fallback if not found
    }
}