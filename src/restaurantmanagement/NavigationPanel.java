// NavigationPanel.java
package restaurantmanagement;

import admincontroller.AdminPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import login_register.LoginSession;

public class NavigationPanel extends JPanel {

    private Main mainFrame;
    private JButton adminButton;

    public NavigationPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(new Color(52, 73, 94));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Create navigation buttons
        JButton homeButton = createNavButton("Trang Chủ", "Home");
        JButton menuButton = createNavButton("Thực Đơn", "Menu");
        JButton reservationButton = createNavButton("Đặt Bàn", "Reservation");
        JButton aboutButton = createNavButton("Về Chúng Tôi", "About");
        JButton contactButton = createNavButton("Liên Hệ", "Contact");

        adminButton = createNavButton("Admin", null);
        JButton logoutButton = createNavButton("Đăng Xuất", null); // This is the button

        // Add buttons to the panel
        add(homeButton);
        add(menuButton);
        add(reservationButton);
        add(aboutButton);
        add(contactButton);

        // Add action listener for Admin button
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    // Check if current user is admin before showing Admin page
                    if (LoginSession.isAdmin()) {
                        mainFrame.showPanel("Admin");
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Bạn không có quyền truy cập trang quản trị.",
                                "Lỗi Quyền Truy Cập",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        add(adminButton); // Add admin button

        // Add action listener for Logout button
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout(); // Call the logout method
            }
        });
        add(logoutButton); // Add logout button

        updateAdminButtonVisibility(); // Initial visibility check
    }

    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94)); // Darker background for consistency
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(76, 110, 143)); // Lighter on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94)); // Back to original
            }
        });

        // Action listener for navigation
        if (panelName != null) {
            button.addActionListener(e -> {
                if (mainFrame != null) {
                    mainFrame.showPanel(panelName);
                }
            });
        }
        return button;
    }

    public void updateAdminButtonVisibility() {
        if (adminButton != null) {
            adminButton.setVisible(LoginSession.isAdmin());
            revalidate();
            repaint();
        }
    }

    private void logout() {
        // Call the handleLogout method in Main to perform a full application reset
        if (mainFrame != null) {
            mainFrame.handleLogout();
        } else {
            System.out.println("Lỗi: mainFrame là null trong NavigationPanel. Không thể chuyển về trang đăng nhập.");
            JOptionPane.showMessageDialog(this,
                    "Không thể trở về trang đăng nhập. Vui lòng khởi động lại ứng dụng.",
                    "Lỗi Đăng Xuất",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}