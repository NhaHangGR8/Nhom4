package restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HomePage extends JPanel { // Changed from JFrame to JPanel
    private String sloganText = "Food For Your Soul";
    private String descriptionText = "Chào mừng quý khách đến với Gr8 Restaurant, nơi ẩm thực và không gian giao hòa. Chúng tôi tự hào mang đến những món ăn được chế biến từ nguyên liệu tươi ngon nhất, kết hợp với công thức độc đáo, tạo nên trải nghiệm ẩm thực khó quên. Hãy đến và cảm nhận sự khác biệt!";
    private List<String> popularDishes;
    private List<String> searchCategories;
    private List<String> services;

    public HomePage() {
        initializeData();
        setupUI();
    }

    private void initializeData() {
        popularDishes = new ArrayList<>();
        popularDishes.add("Hamburger & Khoai tây chiên giòn");
        popularDishes.add("Gà nướng thảo mộc");
        popularDishes.add("Salad vườn tươi mới");
        popularDishes.add("Mì Ý sốt bò băm");

        searchCategories = new ArrayList<>();
        searchCategories.add("Pizza");
        searchCategories.add("Burger");
        searchCategories.add("Mì & Pasta");
        searchCategories.add("Sandwich");
        searchCategories.add("Bít tết");
        searchCategories.add("Hải sản");

        services = new ArrayList<>();
        services.add("Nguyên liệu tươi sạch");
        services.add("Công thức độc quyền");
        services.add("Đầu bếp chuyên nghiệp");
        services.add("Không gian ấm cúng");
        services.add("Menu chay đa dạng");
    }

    private void setupUI() {
        setLayout(new BorderLayout(20, 20)); // Tăng khoảng cách
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(255, 255, 240)); // Light Yellowish Cream - ấm áp hơn

        // --- Banner (North) ---
        JPanel bannerContainer = new JPanel(new BorderLayout());
        bannerContainer.setBackground(new Color(255, 245, 225)); // Slightly darker cream for banner background
        bannerContainer.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 200), 1)); // Viền nhẹ

        URL bannerUrl = getClass().getResource("/resources/images/banner.jpg"); // Giữ nguyên tên này hoặc đổi thành ảnh bạn muốn
        if (bannerUrl == null) { // Fallback if banner.jpg is not found
            // Có thể dùng một ảnh khác từ menu hoặc đầu bếp nếu muốn
            bannerUrl = getClass().getResource("/resources/images/MichaelBaoHuynh.jpg"); // Ví dụ dùng ảnh đầu bếp
            if (bannerUrl == null) {
                System.err.println("Banner image not found. Using default placeholder.");
            }
        }

        if (bannerUrl != null) {
            ImageIcon originalIcon = new ImageIcon(bannerUrl);
            Image originalImage = originalIcon.getImage();
            
            JLabel bannerLabel = new JLabel();
            bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            bannerLabel.setVerticalAlignment(SwingConstants.CENTER);
            
            bannerContainer.add(bannerLabel, BorderLayout.CENTER);

            // Thêm ComponentListener để thay đổi kích thước ảnh khi bannerContainer có kích thước
            bannerContainer.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int panelWidth = bannerContainer.getWidth();
                    int panelHeight = bannerContainer.getHeight();
                    if (panelWidth > 0 && panelHeight > 0) {
                        Image scaledImage = originalImage.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                        bannerLabel.setIcon(new ImageIcon(scaledImage));
                    }
                }
            });

            JPanel sloganOverlayPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    // Để trống, không vẽ gì để nó trong suốt
                }
            };
            sloganOverlayPanel.setOpaque(false); // Quan trọng: làm cho panel trong suốt
            sloganOverlayPanel.setLayout(new GridBagLayout()); // Dùng GridBagLayout để căn giữa

            JLabel slogan = new JLabel(sloganText.toUpperCase(), SwingConstants.CENTER);
            slogan.setFont(new Font("Palatino Linotype", Font.BOLD | Font.ITALIC, 48)); // Font nghệ thuật hơn
            slogan.setForeground(new Color(255, 255, 255, 200)); // Màu trắng hơi trong suốt
            slogan.setOpaque(false); // Đảm bảo JLabel không có nền

            GridBagConstraints gbcSlogan = new GridBagConstraints();
            gbcSlogan.gridx = 0;
            gbcSlogan.gridy = 0;
            gbcSlogan.anchor = GridBagConstraints.CENTER;
            sloganOverlayPanel.add(slogan, gbcSlogan);

            // Sử dụng JLayeredPane để đặt slogan lên trên banner
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(850, 300)); // Kích thước mong muốn cho banner area
            layeredPane.add(bannerContainer, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(sloganOverlayPanel, JLayeredPane.PALETTE_LAYER); // Đặt trên lớp cao hơn

            // Đặt kích thước và vị trí cho các components trong JLayeredPane
            layeredPane.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    bannerContainer.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                    sloganOverlayPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                }
            });

            add(layeredPane, BorderLayout.NORTH);

        } else {
            System.err.println("Banner image not found for HomePage. Using plain slogan.");
            JLabel sloganLabel = new JLabel(sloganText.toUpperCase(), SwingConstants.CENTER);
            sloganLabel.setFont(new Font("Arial", Font.BOLD, 36));
            sloganLabel.setForeground(new Color(139, 69, 19)); // SaddleBrown
            add(sloganLabel, BorderLayout.NORTH);
        }

        // --- Main Content (Center) ---
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0)); // Tăng khoảng cách cột
        contentPanel.setBackground(getBackground());

        // Left Panel for description and popular dishes
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(getBackground());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel descriptionHeader = new JLabel("Câu Chuyện Của Chúng Tôi", SwingConstants.LEFT);
        descriptionHeader.setFont(new Font("Arial", Font.BOLD, 22));
        descriptionHeader.setForeground(new Color(139, 69, 19));
        descriptionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(descriptionHeader);
        leftPanel.add(Box.createVerticalStrut(10));

        JTextArea descriptionArea = new JTextArea(descriptionText);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(getBackground());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descriptionArea.setForeground(new Color(80, 80, 80));
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(descriptionArea);
        leftPanel.add(Box.createVerticalStrut(25));

        JLabel dishesHeader = new JLabel("Món Ăn Được Ưa Chuộng", SwingConstants.LEFT);
        dishesHeader.setFont(new Font("Arial", Font.BOLD, 22));
        dishesHeader.setForeground(new Color(139, 69, 19));
        dishesHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(dishesHeader);

        for (String dish : popularDishes) {
            JLabel dishLabel = new JLabel("• " + dish); // Dùng bullet point
            dishLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            dishLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
            dishLabel.setForeground(new Color(50, 50, 50));
            dishLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(dishLabel);
        }
        leftPanel.add(Box.createVerticalGlue());

        contentPanel.add(leftPanel);

        // Right Panel for search categories and services
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(getBackground());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel categoriesHeader = new JLabel("Tìm Kiếm Theo Danh Mục", SwingConstants.LEFT);
        categoriesHeader.setFont(new Font("Arial", Font.BOLD, 22));
        categoriesHeader.setForeground(new Color(139, 69, 19));
        categoriesHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(categoriesHeader);
        rightPanel.add(Box.createVerticalStrut(10));

        for (String category : searchCategories) {
            JLabel categoryLabel = new JLabel("• " + category);
            categoryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            categoryLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
            categoryLabel.setForeground(new Color(50, 50, 50));
            categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(categoryLabel);
        }
        rightPanel.add(Box.createVerticalStrut(25));

        JLabel servicesHeader = new JLabel("Dịch Vụ Nổi Bật", SwingConstants.LEFT);
        servicesHeader.setFont(new Font("Arial", Font.BOLD, 22));
        servicesHeader.setForeground(new Color(139, 69, 19));
        servicesHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(servicesHeader);
        rightPanel.add(Box.createVerticalStrut(10));

        for (String service : services) {
            JLabel serviceLabel = new JLabel("• " + service);
            serviceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            serviceLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
            serviceLabel.setForeground(new Color(50, 50, 50));
            serviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(serviceLabel);
        }
        rightPanel.add(Box.createVerticalGlue());

        contentPanel.add(rightPanel);

        add(contentPanel, BorderLayout.CENTER);

        // --- Footer (South) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        footerPanel.setBackground(new Color(173, 216, 230)); // LightBlue
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding
        JLabel callToAction = new JLabel("Đặt bàn ngay hôm nay để trải nghiệm ẩm thực đỉnh cao!");
        callToAction.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 19));
        callToAction.setForeground(new Color(50, 50, 50));
        footerPanel.add(callToAction);

        add(footerPanel, BorderLayout.SOUTH);
    }
}