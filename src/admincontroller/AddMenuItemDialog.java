package admincontroller;

import restaurantmanagement.DatabaseHelper;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.border.Border;

public class AddMenuItemDialog extends JDialog { // Đã đổi tên class thành AddDishDialog

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField imagePathField;
    private JButton browseImageButton;
    private JComboBox<String> categoryComboBox;
    private Map<String, String> categoryMap;
    private JButton saveButton;
    private JButton cancelButton;

    private boolean itemSaved = false;

    // Constructor chỉ dành cho việc thêm món mới
    public AddMenuItemDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        initializeCategoryMap();
        setupUI();
        loadCategories();
        setSize(550, 450); // Kích thước phù hợp hơn cho dialog thêm
        setLocationRelativeTo(parent);
        setTitle("Thêm món ăn mới"); // Đặt tiêu đề rõ ràng
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

        add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Lưu");
        cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        browseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(AddMenuItemDialog.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        saveButton.addActionListener(e -> saveDish());
        cancelButton.addActionListener(e -> {
            itemSaved = false;
            dispose();
        });
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
            String sql = "INSERT INTO dishes (name, description, price, image_path, category) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, description.isEmpty() ? null : description);
            pstmt.setDouble(3, price);
            pstmt.setString(4, imagePath.isEmpty() ? null : imagePath);
            pstmt.setString(5, category);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            itemSaved = true;
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            itemSaved = false;
        } finally {
            DatabaseHelper.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isItemSaved() {
        return itemSaved;
    }

    private void loadCategories() {
        // Categories are loaded from the map; this method primarily ensures
        // the combo box is populated correctly.
    }

    private void setBorder(Border createEmptyBorder) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}