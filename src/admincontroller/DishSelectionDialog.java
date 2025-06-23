package admincontroller;

import restaurantmanagement.DatabaseHelper;
import restaurantmanagement.Dish;
import restaurantmanagement.DishOrder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DishSelectionDialog extends JDialog {

    private List<Dish> allDishes;
    private Map<Dish, Integer> selectedDishesMap; // Map to store selected dishes and their quantities
    private JPanel dishesPanel;
    private JScrollPane scrollPane;
    private JLabel totalSelectedLabel;

    public DishSelectionDialog(JFrame parent) {
        super(parent, "Chọn Món Ăn", true); // Modal dialog
        allDishes = new ArrayList<>();
        selectedDishesMap = new HashMap<>();
        loadDishesFromDatabase();
        setupUI();
        updateTotalSelectedLabel();
        setSize(800, 600);
        setLocationRelativeTo(parent);
    }

    private void loadDishesFromDatabase() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT id, name, description, price, category, image_path FROM dishes ORDER BY category, name";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String imagePath = rs.getString("image_path");
                allDishes.add(new Dish(id, name, description, price, imagePath, category));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách món ăn từ CSDL: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách món ăn từ cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chọn Món Ăn Cho Đặt Bàn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        add(titleLabel, BorderLayout.NORTH);

        dishesPanel = new JPanel();
        dishesPanel.setLayout(new BoxLayout(dishesPanel, BoxLayout.Y_AXIS));
        dishesPanel.setBackground(Color.WHITE);

        String currentCategory = "";
        for (Dish dish : allDishes) {
            if (!dish.getCategory().equals(currentCategory)) {
                currentCategory = dish.getCategory();
                JLabel categoryLabel = new JLabel("--- " + getVietnameseCategoryName(currentCategory) + " ---");
                categoryLabel.setFont(new Font("Arial", Font.BOLD, 18));
                categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                categoryLabel.setBorder(new EmptyBorder(15, 0, 5, 0));
                dishesPanel.add(categoryLabel);
            }
            dishesPanel.add(createDishPanel(dish));
        }

        scrollPane = new JScrollPane(dishesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        totalSelectedLabel = new JLabel("Tổng số món đã chọn: 0", SwingConstants.LEFT);
        totalSelectedLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalSelectedLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selectButton = new JButton("Thêm vào đặt bàn");
        selectButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectButton.setBackground(new Color(46, 204, 113));
        selectButton.setForeground(Color.WHITE);
        selectButton.addActionListener(e -> {
            // Dialog closes and selectedDishesMap is accessible via getSelectedDishes()
            dispose();
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> {
            selectedDishesMap.clear(); // Clear selections if cancelled
            dispose();
        });

        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createDishPanel(Dish dish) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(248, 248, 255)); // Light lavender

        // Dish Image (if available)
        JLabel imageLabel = new JLabel();
        if (dish.getImagePath() != null && !dish.getImagePath().isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(dish.getImagePath()));
                Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                imageLabel.setText("No Image");
                imageLabel.setPreferredSize(new Dimension(80, 80));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setVerticalAlignment(SwingConstants.CENTER);
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setPreferredSize(new Dimension(80, 80));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        }
        panel.add(imageLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.setOpaque(false);
        JLabel nameLabel = new JLabel("<html><b>" + dish.getName() + "</b></html>");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel priceLabel = new JLabel(String.format("Giá: %,.0f VNĐ", dish.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Quantity selection
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        quantityPanel.setOpaque(false);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        quantitySpinner.setPreferredSize(new Dimension(60, 30));
        quantitySpinner.setValue(selectedDishesMap.getOrDefault(dish, 0)); // Set initial quantity if already selected

        quantitySpinner.addChangeListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            if (quantity > 0) {
                selectedDishesMap.put(dish, quantity);
            } else {
                selectedDishesMap.remove(dish);
            }
            updateTotalSelectedLabel();
        });
        quantityPanel.add(new JLabel("Số lượng:"));
        quantityPanel.add(quantitySpinner);
        panel.add(quantityPanel, BorderLayout.EAST);

        return panel;
    }

    private void updateTotalSelectedLabel() {
        int totalItems = selectedDishesMap.values().stream().mapToInt(Integer::intValue).sum();
        totalSelectedLabel.setText("Tổng số món đã chọn: " + totalItems);
    }

    public List<DishOrder> getSelectedDishes() {
        List<DishOrder> orders = new ArrayList<>();
        for (Map.Entry<Dish, Integer> entry : selectedDishesMap.entrySet()) {
            orders.add(new DishOrder(entry.getKey(), entry.getValue()));
        }
        return orders;
    }

    private String getVietnameseCategoryName(String englishCategory) {
        return switch (englishCategory) {
            case "main_course" -> "Món chính";
            case "appetizer" -> "Món khai vị";
            case "dessert" -> "Tráng miệng";
            case "beverage" -> "Đồ uống";
            default -> englishCategory;
        };
    }
}