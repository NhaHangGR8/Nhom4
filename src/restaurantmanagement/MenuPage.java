package restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import restaurantmanagement.Dish;
public class MenuPage extends JPanel { // Changed from JFrame to JPanel
    private String titleText = "Thực Đơn Nhà Hàng Gr8";
    private List<Dish> appetizers;
    private List<Dish> maincourses;
    private List<Dish> desserts;
    private List<Dish> beverages;

    public MenuPage() {
        appetizers = new ArrayList<>();
        maincourses = new ArrayList<>();
        desserts = new ArrayList<>();
        beverages = new ArrayList<>();
        loadDishesFromDatabase(); // Đảm bảo DB và bảng `dishes` tồn tại
        setupUI();
    }

    private void loadDishesFromDatabase() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT name, description, price, category, image_path FROM dishes";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String imagePath = rs.getString("image_path");
                Dish dish = new Dish(name, description, price, imagePath);

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
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách món ăn từ CSDL: " + e.getMessage());
            e.printStackTrace();
            // Thêm dữ liệu mẫu nếu không tải được từ DB
            addSampleDishes();
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Thêm dữ liệu mẫu và gán ảnh đã tải lên
    private void addSampleDishes() {
        appetizers.clear();
        maincourses.clear();
        desserts.clear();
        beverages.clear();

        // MÓN KHAI VỊ (Appetizers)
        appetizers.add(new Dish("Bánh Mì Bơ Tỏi", "Bánh mì nướng giòn rụm với hương vị bơ tỏi thơm lừng.", 65.000, "/resources/images/th1.jpg"));
        appetizers.add(new Dish("Salad Trái Cây", "Salad tươi mát với các loại trái cây theo mùa và sốt đặc biệt.", 95.000, "/resources/images/th2.jpg"));
        appetizers.add(new Dish("Súp Bí Đỏ", "Súp bí đỏ sánh mịn, béo ngậy, tốt cho sức khỏe.", 80.000, "/resources/images/th3.jpg"));
        appetizers.add(new Dish("Khoai Tây Chiên Phô Mai", "Khoai tây chiên giòn tan phủ phô mai béo ngậy.", 75.000, "/resources/images/th4.jpg"));

        // MÓN CHÍNH (Main Courses)
        maincourses.add(new Dish("Bò Bít Tết Sốt Tiêu", "Thịt bò thăn hảo hạng, nướng vừa tới, dùng kèm sốt tiêu xanh đậm đà.", 280.000, "/resources/images/th5.jpg"));
        maincourses.add(new Dish("Mì Ý Sốt Kem Nấm", "Sợi mì Ý dai ngon hòa quyện cùng sốt kem nấm truffle thơm lừng.", 160.000, "/resources/images/th6.jpg"));
        maincourses.add(new Dish("Gà Nướng Mật Ong", "Gà nướng nguyên con tẩm ướp mật ong, da giòn, thịt mềm.", 250.000, "/resources/images/th7.jpg"));
        maincourses.add(new Dish("Pizza Hải Sản Cao Cấp", "Đế bánh giòn tan, phủ đầy tôm, mực, nghêu tươi ngon và phô mai.", 190.000, "/resources/images/th8.jpg"));
        maincourses.add(new Dish("Cá Hồi Áp Chảo", "Cá hồi phi lê áp chảo vàng ruộm, giữ trọn vị ngọt tự nhiên.", 220.000, "/resources/images/th9.jpg"));
        maincourses.add(new Dish("Sườn Nướng BBQ", "Sườn non được ướp kỹ và nướng chậm cho đến khi mềm rục, đậm vị.", 270.000, "/resources/images/th10.jpg"));

        // MÓN TRÁNG MIỆNG (Desserts)
        desserts.add(new Dish("Bánh Tiramisu", "Bánh tiramisu truyền thống với hương cà phê và kem Mascarpone béo ngậy.", 90.000, "/resources/images/th11.jpg"));
        desserts.add(new Dish("Bánh Crepe Sầu Riêng", "Bánh crepe mềm mại với nhân sầu riêng tươi thơm lừng.", 85.000, "/resources/images/th12.jpg"));
        desserts.add(new Dish("Kem Các Vị", "Các vị kem homemade đặc biệt, tươi mát.", 70.000, "/resources/images/th13.jpg")); // Tùy chọn ảnh 13.jpg

        // ĐỒ UỐNG (Beverages)
        beverages.add(new Dish("Nước Ép Dưa Hấu", "Nước ép dưa hấu tươi mát, giải khát tức thì.", 45.000, "/resources/images/th14.jpg"));
        beverages.add(new Dish("Mojito Chanh Bạc Hà", "Thức uống cocktail không cồn, thanh mát và sảng khoái.", 60.000, "/resources/images/th15.jpg"));
        beverages.add(new Dish("Cà Phê Sữa Đá", "Cà phê pha phin đậm đà kết hợp sữa đặc.", 50.000, "/resources/images/th16.jpg"));
        beverages.add(new Dish("Sinh Tố Bơ", "Sinh tố bơ sánh mịn, bổ dưỡng.", 55.000, "/resources/images/4.jpg"));
        beverages.add(new Dish("Trà Sữa Trân Châu", "Trà sữa thơm ngon với trân châu dai giòn.", 50.000, "/resources/images/5.jpg"));
        beverages.add(new Dish("Nước Ngọt Coca Cola", "Nước giải khát có ga, sảng khoái.", 30.000, "/resources/images/6.jpg"));
        beverages.add(new Dish("Bia Tiger", "Bia lạnh sảng khoái.", 40.000, "/resources/images/7.jpg"));
        beverages.add(new Dish("Rượu Vang Đỏ", "Ly rượu vang đỏ thượng hạng.", 120.000, "/resources/images/8.jpg"));
        beverages.add(new Dish("Nước Khoáng Lavie", "Nước khoáng tinh khiết.", 20.000, "/resources/images/9.jpg"));
        beverages.add(new Dish("Cocktail Blue Lagoon", "Thức uống đẹp mắt và hấp dẫn.", 90.000, "/resources/images/10.jpg"));
        beverages.add(new Dish("Trà Chanh", "Thức uống giải khát quen thuộc.", 35.000, "/resources/images/11.jpg"));
        beverages.add(new Dish("Sinh Tố Xoài", "Sinh tố xoài tươi ngon.", 55.000, "/resources/images/12.jpg"));
        beverages.add(new Dish("Soda Blue Ocean", "Thức uống soda mát lạnh với màu xanh đại dương.", 50.000, "/resources/images/14.jpg"));
        beverages.add(new Dish("Espresso", "Cà phê Espresso đậm đặc.", 40.000, "/resources/images/15.jpg"));
    }

    private void setupUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(255, 255, 248)); // Rất nhạt, gần trắng

        // --- Add Banner Image for Menu Page ---
        JPanel bannerContainer = new JPanel(new BorderLayout());
        bannerContainer.setBackground(new Color(250, 255, 250)); // Light Greenish-White
        bannerContainer.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 200), 1));

        URL menuBannerUrl = getClass().getResource("/resources/images/page-ban.jpg"); // Giữ nguyên tên này hoặc đổi thành ảnh bạn muốn
        if (menuBannerUrl == null) { // Nếu không tìm thấy ảnh menu-banner.jpg, dùng ảnh 13.jpg (bạn có thể thay đổi)
            menuBannerUrl = getClass().getResource("/resources/images/menu-ban4.jpg"); // Dùng một ảnh menu bạn đã cung cấp
            if (menuBannerUrl == null) { // Nếu vẫn không tìm thấy, dùng default
                System.err.println("Menu banner image not found. Using default placeholder.");
                // Fallback to a plain label or use a default image path if available
            }
        }

        if (menuBannerUrl != null) {
            ImageIcon originalIcon = new ImageIcon(menuBannerUrl);
            Image originalImage = originalIcon.getImage();
            
            JLabel menuBannerLabel = new JLabel();
            menuBannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            menuBannerLabel.setVerticalAlignment(SwingConstants.CENTER);
            
            bannerContainer.add(menuBannerLabel, BorderLayout.CENTER);

            bannerContainer.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int panelWidth = bannerContainer.getWidth();
                    int panelHeight = bannerContainer.getHeight();
                    if (panelWidth > 0 && panelHeight > 0) {
                        Image scaledImage = originalImage.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                        menuBannerLabel.setIcon(new ImageIcon(scaledImage));
                    }
                }
            });
            // Thêm tiêu đề trên ảnh banner
            JPanel titleOverlayPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) { /* Empty for transparency */ }
            };
            titleOverlayPanel.setOpaque(false);
            titleOverlayPanel.setLayout(new GridBagLayout());
            JLabel title = new JLabel(titleText.toUpperCase(), SwingConstants.CENTER);
            title.setFont(new Font("Verdana", Font.BOLD, 42)); // Font lớn, dễ đọc
            title.setForeground(new Color(255, 255, 255, 220)); // White, slightly transparent
            titleOverlayPanel.add(title);

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(850, 250)); // Kích thước mong muốn cho banner
            layeredPane.add(bannerContainer, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(titleOverlayPanel, JLayeredPane.PALETTE_LAYER);

            layeredPane.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    bannerContainer.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                    titleOverlayPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                }
            });

            add(layeredPane, BorderLayout.NORTH);

        } else {
            System.err.println("Menu banner image not found or default image not available. Using plain title.");
            JLabel titleLabel = new JLabel(titleText.toUpperCase(), SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
            titleLabel.setForeground(new Color(139, 69, 19));
            add(titleLabel, BorderLayout.NORTH);
        }

        JPanel menuContentPanel = new JPanel();
        menuContentPanel.setLayout(new BoxLayout(menuContentPanel, BoxLayout.Y_AXIS));
        menuContentPanel.setBackground(getBackground());

        addMenuSection(menuContentPanel, "MÓN KHAI VỊ", appetizers, new Color(255, 245, 230)); // Light Peach
        addMenuSection(menuContentPanel, "MÓN CHÍNH", maincourses, new Color(230, 255, 245)); // Light Aqua
        addMenuSection(menuContentPanel, "MÓN TRÁNG MIỆNG", desserts, new Color(245, 230, 255)); // Light Lavender
        addMenuSection(menuContentPanel, "ĐỒ UỐNG", beverages, new Color(230, 245, 255)); // Light Sky Blue

        JScrollPane scrollPane = new JScrollPane(menuContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addMenuSection(JPanel parentPanel, String sectionTitle, List<Dish> dishes, Color bgColor) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        sectionPanel.setBackground(bgColor);
        sectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa section panel

        JLabel sectionHeader = new JLabel(sectionTitle, SwingConstants.CENTER);
        sectionHeader.setFont(new Font("Arial", Font.BOLD, 28)); // Tiêu đề mục lớn hơn
        sectionHeader.setForeground(new Color(50, 50, 50));
        sectionHeader.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa text trong JLabel
        sectionPanel.add(sectionHeader);
        sectionPanel.add(Box.createVerticalStrut(20));

        if (dishes.isEmpty()) {
            JLabel noDishLabel = new JLabel("Chưa có món nào trong mục này.", SwingConstants.CENTER);
            noDishLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noDishLabel.setForeground(new Color(120, 120, 120));
            noDishLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sectionPanel.add(noDishLabel);
        } else {
            for (Dish dish : dishes) {
                JPanel dishPanel = new JPanel(new BorderLayout(15, 0)); // Tăng khoảng cách
                dishPanel.setBackground(sectionPanel.getBackground());
                dishPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Tăng chiều cao tối đa
                dishPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220))); // Viền dưới nhẹ
                dishPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST); // Padding bên phải

                // Icon món ăn
                URL iconUrl = getClass().getResource(dish.getImagePath());
                ImageIcon dishIcon = null;
                if (iconUrl != null) {
                    dishIcon = new ImageIcon(new ImageIcon(iconUrl).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)); // Icon lớn hơn
                } else {
                    System.err.println("Dish icon not found: " + dish.getImagePath() + ". Using default.");
                    dishIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resources/images/image_da7851.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)); // Icon mặc định
                }
                JLabel iconLabel = new JLabel(dishIcon);
                iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // Padding trái
                dishPanel.add(iconLabel, BorderLayout.WEST);

                // Thông tin món ăn
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(dishPanel.getBackground());

                JLabel nameLabel = new JLabel(dish.getName());
                nameLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Font tên món lớn hơn
                nameLabel.setForeground(new Color(30, 30, 30));
                nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JTextArea descArea = new JTextArea(dish.getDescription());
                descArea.setWrapStyleWord(true);
                descArea.setLineWrap(true);
                descArea.setEditable(false);
                descArea.setBackground(dishPanel.getBackground());
                descArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Font mô tả
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

                sectionPanel.add(dishPanel);
                sectionPanel.add(Box.createVerticalStrut(10)); // Khoảng cách giữa các món
            }
        }
        parentPanel.add(sectionPanel);
        parentPanel.add(Box.createVerticalStrut(35)); // Khoảng cách giữa các mục menu
    }

}