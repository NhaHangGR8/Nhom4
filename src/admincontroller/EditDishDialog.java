package admincontroller;

import restaurantmanagement.DatabaseHelper;
import menu.Dish;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class EditDishDialog extends JDialog {

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField imagePathField;
    private JButton browseImageButton;
    private JComboBox<String> categoryComboBox;
    private Map<String, String> categoryMap;
    private JButton saveButton;
    private JButton cancelButton;

    private int currentDishId = -1; // To store the ID of the dish being edited
    private boolean itemUpdated = false;

    // UI components for selection and form
    private JPanel formPanel;
    private JPanel selectionPanel;
    private JTable dishTable;
    private DefaultTableModel tableModel;
    private JButton selectToEditButton; // Button to switch from table to form
    private JButton backToSelectionButton; // Button to go back to table from form

    public EditDishDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        initializeCategoryMap();
        setupUI();
        loadCategories(); // Ensure categories are loaded for the combo box
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setTitle("Chọn món ăn để chỉnh sửa"); // Initial title
    }

    private void initializeCategoryMap() {
        categoryMap = new LinkedHashMap<>();
        categoryMap.put("Món chính", "main_course");
        categoryMap.put("Món khai vị", "appetizer");
        categoryMap.put("Tráng miệng", "dessert");
        categoryMap.put("Đồ uống", "beverage");
    }

    private void setupUI() {
        setLayout(new CardLayout()); // Use CardLayout to switch between table and form views

        // --- Selection Panel (Table view) ---
        selectionPanel = new JPanel(new BorderLayout());
        setupTableSelectionUI();
        add(selectionPanel, "SelectionPanel"); // Add to CardLayout

        // --- Form Panel (Edit form view) ---
        formPanel = new JPanel(new BorderLayout());
        setupFormUI();
        add(formPanel, "FormPanel"); // Add to CardLayout

        // Initially show the selection panel
        CardLayout cl = (CardLayout)(this.getContentPane().getLayout());
        cl.show(this.getContentPane(), "SelectionPanel");
        loadDishesIntoTable(); // Load dishes when the dialog is opened
    }

    private void setupTableSelectionUI() {
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        selectionPanel.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Tên món", "Danh mục", "Giá"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        dishTable = new JTable(tableModel);
        dishTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dishTable.getTableHeader().setReorderingAllowed(false);
        dishTable.setAutoCreateRowSorter(true);

        JScrollPane tableScrollPane = new JScrollPane(dishTable);
        selectionPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel selectionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selectToEditButton = new JButton("Chọn để sửa");
        selectionButtonPanel.add(selectToEditButton);
        selectionPanel.add(selectionButtonPanel, BorderLayout.SOUTH);

        selectToEditButton.addActionListener(e -> {
            int selectedRow = dishTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = dishTable.convertRowIndexToModel(selectedRow);
                int id = (int) tableModel.getValueAt(modelRow, 0);
                loadDishForEditing(id); // Load details and switch to form view
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn từ bảng để sửa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void setupFormUI() {
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Tên món:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(25);
        fieldsPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        fieldsPanel.add(scrollPane, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Giá (VNĐ):"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(25);
        fieldsPanel.add(priceField, gbc);

        // Image Path
        gbc.gridx = 0;
        gbc.gridy = 3;
        fieldsPanel.add(new JLabel("Đường dẫn ảnh:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imagePathField = new JTextField(20);
        imagePathField.setEditable(false);
        browseImageButton = new JButton("Duyệt...");
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseImageButton, BorderLayout.EAST);
        fieldsPanel.add(imagePanel, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 4;
        fieldsPanel.add(new JLabel("Danh mục:"), gbc);
        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>(categoryMap.keySet().toArray(new String[0]));
        fieldsPanel.add(categoryComboBox, gbc);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Lưu");
        cancelButton = new JButton("Hủy");
        backToSelectionButton = new JButton("Quay lại"); // New button to go back to table
        buttonPanel.add(backToSelectionButton); // Add back button
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners for Form Panel buttons
        browseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(EditDishDialog.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        saveButton.addActionListener(e -> saveDish());
        cancelButton.addActionListener(e -> {
            itemUpdated = false;
            dispose();
        });
        backToSelectionButton.addActionListener(e -> {
            CardLayout cl = (CardLayout)(EditDishDialog.this.getContentPane().getLayout());
            cl.show(EditDishDialog.this.getContentPane(), "SelectionPanel");
            setTitle("Chọn món ăn để chỉnh sửa");
            loadDishesIntoTable(); // Refresh table in case of changes
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
            String sql = "SELECT id, name, category, price FROM dishes ORDER BY name";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String categoryEnglish = rs.getString("category");
                double price = rs.getDouble("price");

                String categoryVietnamese = getVietnameseCategoryName(categoryEnglish);

                Vector<Object> row = new Vector<>();
                row.add(id);
                row.add(name);
                row.add(categoryVietnamese);
                row.add(String.format("%,.0f VNĐ", price));
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

    private void loadDishForEditing(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            String sql = "SELECT id, name, description, price, image_path, category FROM dishes WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Dish dish = new Dish(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("image_path"),
                    rs.getString("category")
                );
                populateFormFields(dish);
                this.currentDishId = dish.getId(); // Set current dishId for saving
                CardLayout cl = (CardLayout)(this.getContentPane().getLayout());
                cl.show(this.getContentPane(), "FormPanel");
                setTitle("Sửa thông tin món ăn"); // Update dialog title
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy món ăn với ID: " + id, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL khi tải món ăn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void populateFormFields(Dish dish) {
        nameField.setText(dish.getName());
        descriptionArea.setText(dish.getDescription());
        priceField.setText(String.valueOf(dish.getPrice()));
        imagePathField.setText(dish.getImagePath());
        String categoryVietnamese = getVietnameseCategoryName(dish.getCategory());
        categoryComboBox.setSelectedItem(categoryVietnamese);
    }

    private void saveDish() {
        String name = nameField.getText();
        String description = descriptionArea.getText();
        double price;
        try {
            price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Giá phải là số dương.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ. Vui lòng nhập một số.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String imagePath = imagePathField.getText();
        String selectedCategoryVietnamese = (String) categoryComboBox.getSelectedItem();
        String category = categoryMap.get(selectedCategoryVietnamese);

        if (name.isEmpty() || category == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên món và chọn Danh mục.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseHelper.getConnection();
            String sql = "UPDATE dishes SET name = ?, description = ?, price = ?, image_path = ?, category = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, description.isEmpty() ? null : description);
            pstmt.setDouble(3, price);
            pstmt.setString(4, imagePath.isEmpty() ? null : imagePath);
            pstmt.setString(5, category);
            pstmt.setInt(6, currentDishId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            itemUpdated = true;
            // After successful update, go back to selection view and refresh table
            CardLayout cl = (CardLayout)(this.getContentPane().getLayout());
            cl.show(this.getContentPane(), "SelectionPanel");
            setTitle("Chọn món ăn để chỉnh sửa");
            loadDishesIntoTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            itemUpdated = false;
        } finally {
            DatabaseHelper.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isItemUpdated() {
        return itemUpdated;
    }

    private void loadCategories() {
        // Categories are loaded from the map
    }

    private String getVietnameseCategoryName(String englishCategory) {
        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            if (entry.getValue().equals(englishCategory)) {
                return entry.getKey();
            }
        }
        return englishCategory;
    }
}