package restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// import admincontroller.AddMenuItemDialog; // Removed this import, as AddMenuItemDialog is split
import admincontroller.AddMenuItemDialog; // Import the new AddDishDialog
import admincontroller.EditDishDialog; // Import EditDishDialog (though MenuPage doesn't directly call it)
import admincontroller.DeleteDishDialog; // Import DeleteDishDialog (though MenuPage doesn't directly call it)
import java.io.File;


public class MenuPage extends JPanel {
    private String titleText = "Thực Đơn Nhà Hàng Gr8";
    private List<Dish> appetizers;
    private List<Dish> maincourses;
    private List<Dish> desserts;
    private List<Dish> beverages;

    private JPanel menuContentPanel; // Keep a reference to the content panel
    private Dish selectedDish = null; // To store the currently selected dish for editing
    private JPanel selectedDishPanel = null; // To store the panel of the selected dish for visual feedback

    public MenuPage() {
        appetizers = new ArrayList<>();
        maincourses = new ArrayList<>();
        desserts = new ArrayList<>();
        beverages = new ArrayList<>();
        setupUI();
        loadDishesFromDatabase(); // Load dishes after UI is set up
    }

    private void loadDishesFromDatabase() {
        // Clear existing dishes before loading
        appetizers.clear();
        maincourses.clear();
        desserts.clear();
        beverages.clear();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT id, name, description, price, category, image_path FROM dishes ORDER BY category, name"; // Order by category and name for consistent display
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String imagePath = rs.getString("image_path");

                Dish dish = new Dish(id, name, description, price, imagePath, category);

                switch (category) {
                    case "appetizer":
                        appetizers.add(dish);
                        break;
                    case "main_course":
                        maincourses.add(dish);
                        break;
                    case "dessert":
                        desserts.add(dish);
                        break;
                    case "beverage":
                        beverages.add(dish);
                        break;
                    default:
                        // Handle unknown categories or add to a default list
                        break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải món ăn từ cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu thực đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 253, 247)); // Light beige background

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(173, 216, 230)); // Light blue
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(34, 139, 34)); // ForestGreen
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Menu Content Panel (where dishes will be displayed)
        menuContentPanel = new JPanel();
        menuContentPanel.setLayout(new BoxLayout(menuContentPanel, BoxLayout.Y_AXIS));
        menuContentPanel.setBackground(new Color(255, 253, 247));
        menuContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(menuContentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Make scrolling smoother
        add(scrollPane, BorderLayout.CENTER);

        // Add categories and dishes
        populateMenuContent();

        // Add a ComponentListener to re-populate menu when the window is resized/shown
        // This is important for dynamic loading or when panels might be re-rendered
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // To optimize, you might only revalidate/repaint without re-populating everything
                // if the layout manager handles resizing well.
                // For now, repopulating ensures everything is drawn correctly.
                // If there are many items, consider more efficient updates.
                // refreshMenu(); // Avoid calling refreshMenu too often on resize
            }

            @Override
            public void componentShown(ComponentEvent e) {
                refreshMenu(); // Refresh when the page becomes visible
            }
        });
    }

    private void populateMenuContent() {
        menuContentPanel.removeAll(); // Clear existing content

        // Appetizers
        addCategorySection(menuContentPanel, "MÓN KHAI VỊ", appetizers);
        // Main Courses
        addCategorySection(menuContentPanel, "MÓN CHÍNH", maincourses);
        // Desserts
        addCategorySection(menuContentPanel, "TRÁNG MIỆNG", desserts);
        // Beverages
        addCategorySection(menuContentPanel, "ĐỒ UỐNG", beverages);

        menuContentPanel.revalidate();
        menuContentPanel.repaint();
    }

    private void addCategorySection(JPanel parentPanel, String categoryTitle, List<Dish> dishes) {
        if (dishes.isEmpty()) {
            return; // Don't add section if no dishes in category
        }

        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(parentPanel.getBackground());
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel(categoryTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 0)); // Dark Green
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(15)); // Space after title

        for (Dish dish : dishes) {
            JPanel dishPanel = createDishPanel(dish);
            sectionPanel.add(dishPanel);
            sectionPanel.add(Box.createVerticalStrut(10)); // Khoảng cách giữa các món
        }
        parentPanel.add(sectionPanel);
        parentPanel.add(Box.createVerticalStrut(35)); // Khoảng cách giữa các mục menu
    }

    private JPanel createDishPanel(Dish dish) {
        JPanel dishPanel = new JPanel(new BorderLayout(10, 0));
        dishPanel.setBackground(Color.WHITE); // White background for each dish item
        dishPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Light gray border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside border
        ));
        dishPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dishPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Limit height

        // Image (Left)
        JLabel imageLabel = new JLabel();
        try {
            String imagePath = dish.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl == null) {
                    // Fallback for absolute paths or development environment
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        imageUrl = imgFile.toURI().toURL();
                    } else {
                        System.err.println("Image not found at: " + imagePath);
                        // Fallback to a default image or simply leave it blank
                        // imageUrl = getClass().getResource("/images/default_dish.png");
                    }
                }

                if (imageUrl != null) {
                    ImageIcon icon = new ImageIcon(imageUrl);
                    Image image = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Scale image
                    imageLabel.setIcon(new ImageIcon(image));
                }
            } else {
                // No image path provided, use a default placeholder or leave blank
                // imageLabel.setIcon(new ImageIcon(getClass().getResource("/images/no_image.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image for " + dish.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Padding phải
        dishPanel.add(imageLabel, BorderLayout.WEST);

        // Info (Center)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(dishPanel.getBackground());
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tên món
        JLabel nameLabel = new JLabel(dish.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(new Color(30, 30, 30));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(dish.getDescription());
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setBackground(dishPanel.getBackground());
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setForeground(new Color(90, 90, 90));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Giới hạn chiều cao

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(descArea);
        dishPanel.add(infoPanel, BorderLayout.CENTER);

        // Giá
        JLabel priceLabel = new JLabel(String.format("%,.0f VNĐ", dish.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Font giá lớn và đậm hơn
        priceLabel.setForeground(new Color(220, 50, 50)); // Màu đỏ nổi bật
        priceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); // Padding phải
        dishPanel.add(priceLabel, BorderLayout.EAST);

        return dishPanel;
    }

    /**
     * Refreshes the menu by clearing existing dishes, reloading from the database,
     * and re-populating the UI. This method should be called after any
     * add, edit, or delete operations on dishes.
     */
    public void refreshMenu() {
        loadDishesFromDatabase(); // Reload data
        populateMenuContent();    // Re-populate UI components
    }

    public List<Dish> getAppetizers() {
        return appetizers;
    }

    public List<Dish> getMainCourses() {
        return maincourses;
    }

    public List<Dish> getDesserts() {
        return desserts;
    }

    public List<Dish> getBeverages() {
        return beverages;
    }
}