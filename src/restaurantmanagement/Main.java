package restaurantmanagement;

import admincontroller.AdminPage; // Import AdminPage
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private NavigationPanel navigationPanel; // Khai báo thanh điều hướng

    // Khai báo các trang/panel
    private LoginPage loginPage;
    private RegisterPage registerPage;
    private HomePage homePage;
    private MenuPage menuPage;
    private ReservationPage reservationPage;
    private AboutPage aboutPage;
    private ContactPage contactPage;

    public Main() {
        setTitle("Hệ Thống Quản Lý Nhà Hàng GR8");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Khởi tạo CardLayout và Panel chứa các card
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Khởi tạo các trang/panel, truyền tham chiếu 'this' (Main frame) nếu cần
        loginPage = new LoginPage(this);
        registerPage = new RegisterPage(this);
        homePage = new HomePage();
        menuPage = new MenuPage();
        reservationPage = new ReservationPage();
        aboutPage = new AboutPage();
        contactPage = new ContactPage();

        // Thêm các panel vào cardPanel với tên định danh
        cardPanel.add(loginPage, "Login");
        cardPanel.add(registerPage, "Register");
        cardPanel.add(homePage, "Home");
        cardPanel.add(menuPage, "Menu");
        cardPanel.add(reservationPage, "Reservation");
        cardPanel.add(aboutPage, "About");
        cardPanel.add(contactPage, "Contact");

        // Khởi tạo NavigationPanel và thêm nó vào phần NORTH của frame
        navigationPanel = new NavigationPanel(this);
        add(navigationPanel, BorderLayout.NORTH);
        
        // Thêm cardPanel vào trung tâm của JFrame
        add(cardPanel, BorderLayout.CENTER);

        // Hiển thị panel mặc định khi khởi động ứng dụng
        showPanel("Login"); // Bắt đầu với trang đăng nhập

        setVisible(true);
    }

    // Phương thức để chuyển đổi panel và điều khiển hiển thị thanh điều hướng
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        // Ẩn thanh điều hướng cho LoginPage và RegisterPage
        if ("Login".equals(panelName) || "Register".equals(panelName)) {
            navigationPanel.setVisible(false);
        } else {
            navigationPanel.setVisible(true);
        }
        // Gọi updateAdminButtonVisibility mỗi khi chuyển đổi panel
        // Điều này đảm bảo nút Admin sẽ ẩn/hiện đúng cách sau khi đăng nhập/đăng xuất
        navigationPanel.updateAdminButtonVisibility(); 
        revalidate(); // Cập nhật lại layout
        repaint();    // Vẽ lại frame
    }

    // Phương thức này hiện tại được xử lý bởi logic trong showPanel, nhưng được giữ lại nếu có nơi khác gọi đến.
    void setNavigationPanelVisible(boolean visible) {
        navigationPanel.setVisible(visible);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Main();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Đã xảy ra lỗi không mong muốn khi khởi chạy ứng dụng: " + e.getMessage(),
                        "Lỗi Khởi Chạy",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}