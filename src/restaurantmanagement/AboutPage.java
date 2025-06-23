package restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent; // Import ComponentEvent

public class AboutPage extends JPanel {
    private String titleText = "Về Chúng Tôi";
    private String restaurantStoryTitle = "Câu Chuyện Của Nhà Hàng Gr8";
    private String restaurantStory = "Nhà hàng Gr8 được sáng lập bởi những người đam mê ẩm thực với tiêu chí mang đến những món ăn ngon nhất và trải nghiệm tốt nhất cho khách hàng. Chúng tôi luôn sử dụng những nguyên liệu tươi ngon, được chọn lọc kỹ càng, và áp dụng công thức độc đáo để tạo nên những bữa ăn đáng nhớ. Từ những bước chân đầu tiên, Gr8 Restaurant đã không ngừng nỗ lực để trở thành điểm đến lý tưởng cho những ai yêu thích sự tinh tế trong từng hương vị.";
    private List<Chef> chefs;


    public AboutPage() {
        chefs = new ArrayList<>();
        loadChefsFromDatabase(); // Đảm bảo DB và bảng `chefs` tồn tại
        setupUI();
    }

    private void loadChefsFromDatabase() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            stmt = conn.createStatement();
            // Đảm bảo cột 'image_path' tồn tại trong bảng 'chefs' nếu muốn dùng ảnh
            String sql = "SELECT name, experience, image_path FROM chefs";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                String experience = rs.getString("experience");
                String imagePath = rs.getString("image_path"); // Lấy đường dẫn ảnh
                chefs.add(new Chef(name, experience, imagePath));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách đầu bếp từ CSDL: " + e.getMessage());
            e.printStackTrace();
            // Thêm dữ liệu mẫu nếu không tải được từ DB hoặc lỗi
            addSampleChefs();
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void addSampleChefs() {
        chefs.clear(); // Xóa dữ liệu cũ nếu có
        chefs.add(new Chef("Christine Hà", "Là quán quân Vua đầu bếp MasterChef Mỹ 2012, nổi tiếng với khả năng nấu ăn xuất sắc dù bị khiếm thị. Tham gia nhiều chương trình ẩm thực lớn trên thế giới và là tác giả sách nấu ăn bán chạy.", "/resources/images/ChristineHa.jpg"));
        chefs.add(new Chef("Luke Nguyễn", "Đầu bếp nổi tiếng người Úc gốc Việt, chủ nhà hàng ở Sydney và nhiều chương trình truyền hình về ẩm thực du lịch. Chuyên về món ăn Việt Nam truyền thống và hiện đại.", "/resources/images/LukeNguyen.jpg"));
        chefs.add(new Chef("Michael Bảo Huỳnh", "Đầu bếp người Mỹ gốc Việt, từng làm việc tại nhiều nhà hàng Michelin Star và được biết đến với phong cách nấu ăn sáng tạo, kết hợp ẩm thực Á-Âu.", "/resources/images/MichaelBaoHuynh.jpg"));
    }

    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Sắp xếp theo chiều dọc
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // Padding tổng thể
        setBackground(new Color(245, 245, 220)); // Light Khaki - nền nhẹ nhàng
        setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa nội dung

        // Tiêu đề trang
        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(new Color(139, 69, 19)); // SaddleBrown
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(30)); // Khoảng cách

        // Câu chuyện nhà hàng
        JLabel restaurantStoryTitleLabel = new JLabel(restaurantStoryTitle, SwingConstants.CENTER);
        restaurantStoryTitleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        restaurantStoryTitleLabel.setForeground(new Color(85, 107, 47)); // DarkOliveGreen
        restaurantStoryTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(restaurantStoryTitleLabel);
        add(Box.createVerticalStrut(15));

        JTextArea restaurantStoryArea = new JTextArea(restaurantStory);
        restaurantStoryArea.setFont(new Font("Arial", Font.PLAIN, 16));
        restaurantStoryArea.setWrapStyleWord(true);
        restaurantStoryArea.setLineWrap(true);
        restaurantStoryArea.setEditable(false);
        restaurantStoryArea.setBackground(getBackground());
        restaurantStoryArea.setForeground(new Color(50, 50, 50));
        restaurantStoryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        restaurantStoryArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, restaurantStoryArea.getPreferredSize().height + 50)); // Allow some vertical growth
        add(new JScrollPane(restaurantStoryArea)); // Thêm vào JScrollPane để cuộn nếu cần

        add(Box.createVerticalStrut(30));

        // Khu vực đầu bếp
        JLabel chefsHeader = new JLabel("Đội Ngũ Đầu Bếp Tài Năng Của Chúng Tôi", SwingConstants.CENTER);
        chefsHeader.setFont(new Font("Arial", Font.BOLD, 26));
        chefsHeader.setForeground(new Color(85, 107, 47)); // DarkOliveGreen
        chefsHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(chefsHeader);
        add(Box.createVerticalStrut(20));

        JPanel chefsPanel = new JPanel();
        chefsPanel.setLayout(new GridLayout(0, 3, 20, 20)); // 3 cột, tự động số hàng, khoảng cách 20px
        chefsPanel.setBackground(getBackground());
        chefsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding cho panel đầu bếp

        for (Chef chef : chefs) {
            chefsPanel.add(createChefEntryPanel(chef));
        }
        add(new JScrollPane(chefsPanel)); // ScrollPane cho danh sách đầu bếp
        add(Box.createVerticalStrut(30));

        // Motto Section
        JLabel closingRemark = new JLabel("Nơi Hương Vị Bắt Đầu, Nơi Ký Ức Đọng Lại!", SwingConstants.CENTER);
        closingRemark.setFont(new Font("Serif", Font.ITALIC, 20));
        closingRemark.setAlignmentX(Component.CENTER_ALIGNMENT);
        closingRemark.setForeground(new Color(80, 80, 80));
        add(closingRemark);
    }

    private JPanel createChefEntryPanel(Chef chef) {
        JPanel chefEntry = new JPanel(new BorderLayout(10, 10));
        chefEntry.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 160), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        chefEntry.setBackground(new Color(255, 255, 250)); // Very light cream for each chef entry

        // Chef Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        // Removed setPreferredSize here to allow image scaling
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(chefEntry.getBackground());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        chefEntry.add(imagePanel, BorderLayout.WEST);

        URL imageUrl = getClass().getResource(chef.getImagePath());
        if (imageUrl != null) {
            try {
                Image originalChefImage = new ImageIcon(imageUrl).getImage();
                imagePanel.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        int width = imagePanel.getWidth();
                        int height = imagePanel.getHeight();
                        if (originalChefImage != null && width > 0 && height > 0) {
                            Image scaledImage = getScaledImage(originalChefImage, width, height);
                            imageLabel.setIcon(new ImageIcon(scaledImage));
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Error loading chef image: " + chef.getName() + " - " + e.getMessage());
                imageLabel.setText("Image Error");
                imageLabel.setForeground(Color.RED);
            }
        } else {
            imageLabel.setText("Image Not Found");
            imageLabel.setForeground(Color.RED);
        }

        // Chef Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(chefEntry.getBackground());
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(chef.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(new Color(70, 70, 70));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea experienceArea = new JTextArea(chef.getExperience());
        experienceArea.setWrapStyleWord(true);
        experienceArea.setLineWrap(true);
        experienceArea.setEditable(false);
        experienceArea.setBackground(infoPanel.getBackground());
        experienceArea.setFont(new Font("Arial", Font.ITALIC, 14));
        experienceArea.setForeground(new Color(80, 80, 80));
        experienceArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(experienceArea);
        chefEntry.add(infoPanel, BorderLayout.CENTER);

        return chefEntry;
    }

    // Utility method to scale image
    private Image getScaledImage(Image srcImg, int w, int h) {
        // Maintain aspect ratio
        int originalWidth = srcImg.getWidth(null);
        int originalHeight = srcImg.getHeight(null);

        if (originalWidth == -1 || originalHeight == -1) { // Image not loaded yet
            return srcImg;
        }

        double widthRatio = (double) w / originalWidth;
        double heightRatio = (double) h / originalHeight;

        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        // Ensure minimum size if needed, or prevent too small images
        if (newWidth == 0 || newHeight == 0) {
            newWidth = 1; // Prevent 0 dimension, might cause issues
            newHeight = 1;
        }

        return srcImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    public static class Chef {
        private String name;
        private String experience;
        private String imagePath; // Thêm đường dẫn ảnh

        public Chef(String name, String experience, String imagePath) {
            this.name = name;
            this.experience = experience;
            this.imagePath = imagePath;
        }

        public String getName() { return name; }
        public String getExperience() { return experience; }
        public String getImagePath() { return imagePath; }
    }
}