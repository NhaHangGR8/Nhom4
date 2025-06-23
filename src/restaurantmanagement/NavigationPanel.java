package restaurantmanagement;

import admincontroller.AdminPage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        JButton logoutButton = createNavButton("Đăng Xuất", null);

        // Add buttons to the panel
        add(homeButton);
        add(menuButton);
        add(reservationButton);
        add(aboutButton);
        add(contactButton);
        add(adminButton);
        add(logoutButton);

        updateAdminButtonVisibility();
    }

    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(76, 110, 142));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panelName != null) {
                    mainFrame.showPanel(panelName);
                } else {
                    if (text.equals("Đăng Xuất")) {
                        logout();
                    } else if (text.equals("Admin")) {
                        if (LoginSession.isAdmin()) {
                            AdminPage adminPage = new AdminPage();
                            adminPage.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(mainFrame,
                                "Bạn không có quyền truy cập trang quản trị.",
                                "Lỗi Quyền Truy Cập",
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });

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
        SwingUtilities.getWindowAncestor(this).dispose();
        new LoginPage(); // Gọi constructor không đối số
    }
}
