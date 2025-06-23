package restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ContactPage extends JPanel {
    private String titleText = "Liên Hệ Với Chúng Tôi";
    private String contactHeader = "Chúng tôi luôn sẵn lòng hỗ trợ bạn!";
    private String address = "Số 3, Đường Cầu Giấy, Quận Đống Đa, Hà Nội";
    private String phone = "+84 1900 1900 (Hỗ trợ 24/7)";
    private String email = "hotro.gr8restaurant@email.com";
    private String workingHours = "Thứ 2 - Thứ 6: 08:00 - 22:00\nThứ 7 - Chủ Nhật: 09:00 - 23:00";

    public ContactPage() {
        setupUI();
    }

    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Increased padding
        setBackground(new Color(245, 250, 255)); // Very light bluish-white

        // Title
        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38)); // Larger and bolder
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(50, 70, 90)); // Darker blue-gray
        add(titleLabel);
        add(Box.createVerticalStrut(25)); // Space after title

        // Header
        JLabel headerLabel = new JLabel(contactHeader, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.ITALIC, 22)); // Italic and slightly larger
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setForeground(new Color(80, 80, 80));
        add(headerLabel);
        add(Box.createVerticalStrut(40)); // Space after header

        // Contact Info Panel
        JPanel contactInfoPanel = new JPanel();
        contactInfoPanel.setLayout(new GridBagLayout());
        contactInfoPanel.setBackground(new Color(255, 255, 255)); // White background for info
        contactInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240), 1), // Light border
                BorderFactory.createEmptyBorder(25, 30, 25, 30) // Internal padding
        ));
        contactInfoPanel.setMaximumSize(new Dimension(700, Short.MAX_VALUE)); // Limit width
        contactInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0); // Padding between rows
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Address
        gbc.gridy = 0;
        contactInfoPanel.add(createInfoLabel(address, loadIcon("/resources/images/location.png", 30, 30)), gbc);

        // Phone
        gbc.gridy = 1;
        contactInfoPanel.add(createInfoLabel(phone, loadIcon("/resources/images/phone.png", 30, 30)), gbc);

        // Email
        gbc.gridy = 2;
        contactInfoPanel.add(createInfoLabel(email, loadIcon("/resources/images/email.png", 30, 30)), gbc);

        add(contactInfoPanel);
        add(Box.createVerticalStrut(40)); // Space after contact info

        // Working Hours Section
        JPanel hoursPanel = new JPanel();
        hoursPanel.setLayout(new BoxLayout(hoursPanel, BoxLayout.Y_AXIS));
        hoursPanel.setBackground(new Color(255, 255, 255)); // White background for hours
        hoursPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240), 1), // Light border
                BorderFactory.createEmptyBorder(25, 30, 25, 30) // Internal padding
        ));
        hoursPanel.setMaximumSize(new Dimension(700, Short.MAX_VALUE)); // Limit width
        hoursPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hoursHeader = new JLabel("Giờ Mở Cửa", SwingConstants.CENTER);
        hoursHeader.setFont(new Font("Arial", Font.BOLD, 26)); // Larger and bolder
        hoursHeader.setForeground(new Color(70, 90, 110));
        hoursHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        hoursPanel.add(hoursHeader);
        hoursPanel.add(Box.createVerticalStrut(15));

        JTextArea hoursArea = new JTextArea(workingHours);
        hoursArea.setFont(new Font("Monospaced", Font.PLAIN, 18)); // Monospaced for consistent alignment
        hoursArea.setForeground(new Color(60, 60, 60));
        hoursArea.setBackground(new Color(255, 255, 255));
        hoursArea.setEditable(false);
        hoursArea.setWrapStyleWord(true);
        hoursArea.setLineWrap(true);
        hoursArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        hoursArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding for text area
        hoursPanel.add(hoursArea);
        add(hoursPanel);
        add(Box.createVerticalStrut(35)); // Space before closing remark

        JLabel closingRemark = new JLabel("Cảm ơn bạn đã ghé thăm! Hy vọng được phục vụ bạn!", SwingConstants.CENTER); // More engaging
        closingRemark.setFont(new Font("Serif", Font.ITALIC, 22)); // Slightly larger
        closingRemark.setAlignmentX(Component.CENTER_ALIGNMENT);
        closingRemark.setForeground(new Color(80, 80, 80));
        add(closingRemark);
    }

    private JLabel createInfoLabel(String text, ImageIcon icon) {
        JLabel label;
        if (icon != null) {
            label = new JLabel(text, icon, SwingConstants.LEFT);
        } else {
            label = new JLabel(text);
        }
        label.setFont(new Font("Arial", Font.PLAIN, 19)); // Larger font size
        label.setForeground(new Color(60, 60, 60));
        label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // More padding on left
        return label;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        URL iconUrl = getClass().getResource(path);
        if (iconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(iconUrl);
            Image image = originalIcon.getImage();
            return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } else {
            System.err.println("Icon not found: " + path);
            return null;
        }
    }
}