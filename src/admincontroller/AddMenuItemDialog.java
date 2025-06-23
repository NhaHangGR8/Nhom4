package admincontroller;

import restaurantmanagement.DatabaseHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap; // Use LinkedHashMap to preserve insertion order
import java.util.Map;

public class AddMenuItemDialog extends JDialog {

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField imagePathField;
    private JButton browseImageButton;
    private JComboBox<String> categoryComboBox; // ComboBox hiển thị tên tiếng Việt
    private Map<String, String> categoryMap; // Map để ánh xạ tên tiếng Việt sang tiếng Anh
    private JButton saveButton;
    private JButton cancelButton;

    private int dishId = -1; // -1 for new dish, otherwise for existing dish

    public AddMenuItemDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        initializeCategoryMap();
        setupUI();
        loadCategories();
    }

    public AddMenuItemDialog(JFrame parent, String title, boolean modal, int dishId, String name, String description, double price, String imagePath, String category) {
        super(parent, title, modal);
        this.dishId = dishId;
        initializeCategoryMap();
        setupUI();
        nameField.setText(name);
        descriptionArea.setText(description);
        priceField.setText(String.valueOf(price));
        imagePathField.setText(imagePath);
        loadCategories();
        // Set selected item based on the English category name, but display Vietnamese
        String vietnameseCategory = getVietnameseCategoryName(category);
        if (vietnameseCategory != null) {
            categoryComboBox.setSelectedItem(vietnameseCategory);
        }
    }

    private void initializeCategoryMap() {
        categoryMap = new LinkedHashMap<>();
        categoryMap.put("Món chính", "main_course");
        categoryMap.put("Món khai vị", "appetizer");
        categoryMap.put("Tráng miệng", "dessert");
        categoryMap.put("Đồ uống", "beverage");
    }

    private String getVietnameseCategoryName(String englishCategory) {
        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            if (entry.getValue().equals(englishCategory)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getEnglishCategoryName(String vietnameseCategory) {
        return categoryMap.get(vietnameseCategory);
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Tên món ăn:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(20);
        add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Đường dẫn ảnh:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePathField = new JTextField(15);
        imagePathField.setEditable(false);
        browseImageButton = new JButton("Duyệt...");
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseImageButton, BorderLayout.EAST);
        add(imagePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Loại món ăn:"), gbc);
        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setEditable(false); // Không cho phép nhập loại mới
        add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton = new JButton("Lưu");
        cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);

        browseImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn ảnh món ăn");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
                int userSelection = fileChooser.showOpenDialog(AddMenuItemDialog.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMenuItem();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(getParent());
    }

    private void loadCategories() {
        categoryComboBox.removeAllItems();
        // Add categories from the map (displaying Vietnamese names)
        for (String vietnameseName : categoryMap.keySet()) {
            categoryComboBox.addItem(vietnameseName);
        }
    }

    private void saveMenuItem() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceText = priceField.getText().trim();
        String imagePath = imagePathField.getText().trim();
        // Get the selected Vietnamese category name
        String selectedVietnameseCategory = (String) categoryComboBox.getSelectedItem();
        // Convert to English category name for database storage
        String category = getEnglishCategoryName(selectedVietnameseCategory);

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên món ăn và giá.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Giá không thể là số âm.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ. Vui lòng nhập một số.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseHelper.getConnection();
            String sql;
            if (dishId == -1) {
                sql = "INSERT INTO dishes (name, description, price, image_path, category) VALUES (?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, description.isEmpty() ? null : description);
                pstmt.setDouble(3, price);
                pstmt.setString(4, imagePath.isEmpty() ? null : imagePath);
                pstmt.setString(5, category); // Use the English category name
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                sql = "UPDATE dishes SET name = ?, description = ?, price = ?, image_path = ?, category = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, description.isEmpty() ? null : description);
                pstmt.setDouble(3, price);
                pstmt.setString(4, imagePath.isEmpty() ? null : imagePath);
                pstmt.setString(5, category); // Use the English category name
                pstmt.setInt(6, dishId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}