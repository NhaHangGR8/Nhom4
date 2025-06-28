// Main.java
package restaurantmanagement;

import admincontroller.AdminPage; // Import AdminPage
import admincontroller.TableListPage; // Import TableListPage
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import login_register.LoginPage;
import login_register.RegisterPage;
import menu.MenuPage;
import login_register.LoginSession; // Import LoginSession

public class Main extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private NavigationPanel navigationPanel; // Khai báo thanh điều hướng

    // Khai báo các trang/panel (these will be re-instantiated in setupApplicationPanels)
    private LoginPage loginPage;
    private RegisterPage registerPage;
    private HomePage homePage;
    private MenuPage menuPage;
    private ReservationPage reservationPage;
    private AboutPage aboutPage;
    private ContactPage contactPage;
    private AdminPage adminPage; // Declare AdminPage here

    // Declare a single instance of TableListPage to be shared
    private TableListPage tableListPageInstance; // NEW: Declare TableListPage instance

    public Main() {
        setTitle("Nhà Hàng GR8");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Khởi tạo CardLayout và Panel chứa các card
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initial setup of application panels
        setupApplicationPanels();

        // Khởi tạo NavigationPanel và thêm nó vào phần NORTH của frame
        // NavigationPanel needs a reference to Main to call showPanel or handleLogout
        navigationPanel = new NavigationPanel(this); // Pass 'this'
        add(navigationPanel, BorderLayout.NORTH);

        // Thêm cardPanel vào trung tâm của JFrame
        add(cardPanel, BorderLayout.CENTER);

        // Hiển thị panel mặc định khi khởi động ứng dụng
        showPanel("Login");

        setVisible(true);
    }

    // New method to initialize/reset all application panels
    public void setupApplicationPanels() {
        // Clear existing panels if any, for a clean refresh
        cardPanel.removeAll();

        // NEW: Initialize TableListPage here, ensuring it's a single instance shared
        if (tableListPageInstance == null) {
            tableListPageInstance = new TableListPage();
        } else {
            // If it already exists, refresh its content to reflect current data
            // This is crucial for refreshing table status after payment or other updates
            tableListPageInstance.refreshTableStatus();
        }

        // Khởi tạo các trang/panel, truyền tham chiếu 'this' (Main frame) nếu cần
        loginPage = new LoginPage(this); // LoginPage needs Main frame for navigation
        registerPage = new RegisterPage(this);
        homePage = new HomePage();
        menuPage = new MenuPage();
        reservationPage = new ReservationPage(); // ReservationPage instance

        aboutPage = new AboutPage();
        contactPage = new ContactPage();

        // AdminPage now takes both ReservationPage and TableListPage
        adminPage = new AdminPage(reservationPage, tableListPageInstance); // UPDATED: Pass reservationPage and tableListPageInstance

        // Thêm các panel vào cardPanel với tên định danh
        cardPanel.add(loginPage, "Login");
        cardPanel.add(registerPage, "Register");
        cardPanel.add(homePage, "Home");
        cardPanel.add(menuPage, "Menu");
        cardPanel.add(reservationPage, "Reservation");
        cardPanel.add(aboutPage, "About");
        cardPanel.add(contactPage, "Contact");
        cardPanel.add(adminPage, "Admin"); // Add AdminPage

        // Revalidate and repaint to ensure UI updates
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // Phương thức điều khiển hiển thị thanh điều hướng
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

    // Phương thức để xử lý đăng xuất
    public void handleLogout() {
        LoginSession.logout(); // Clear login session
        setupApplicationPanels(); // Re-initialize all panels for a clean state
        showPanel("Login"); // Show login page after logout
        JOptionPane.showMessageDialog(this, "Bạn đã đăng xuất thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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