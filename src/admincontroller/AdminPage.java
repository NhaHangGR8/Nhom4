package admincontroller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import restaurantmanagement.ReservationPage;
import restaurantmanagement.TableStatusRefreshListener; // Import the interface

public class AdminPage extends JFrame {

    // AdminPage needs a reference to the ReservationPage instance
    // that the user is currently viewing, to enable refresh.
    private ReservationPage reservationPageInstance; // Field to hold the reference

    // NEW: Field to hold the TableListPage instance
    private TableListPage tableListPageInstance;

    // Modified constructor to accept ReservationPage
    public AdminPage(ReservationPage reservationPage) {
        this.reservationPageInstance = reservationPage; // Store the ReservationPage instance

        // NEW: Initialize the TableListPage instance here
        this.tableListPageInstance = new TableListPage(); // Create a single instance

        setTitle("Trang quản trị Admin");
        setSize(450, 400); // Tăng kích thước để phù hợp với các nút mới
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 15, 15)); // Thay đổi GridLayout thành 5 hàng, 1 cột

        // Cấu hình các nút với style nhất quán
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Dimension buttonSize = new Dimension(250, 50); // Kích thước cố định cho nút

        JButton addDishButton = new JButton("Thêm/Sửa món ăn");
        addDishButton.setFont(buttonFont);
        addDishButton.setPreferredSize(buttonSize);
        addDishButton.setBackground(new Color(144, 238, 144)); // LightGreen
        addDishButton.setForeground(Color.BLACK);
        addDishButton.setFocusPainted(false);
        addDishButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton viewStatsButton = new JButton("Xem thống kê");
        viewStatsButton.setFont(buttonFont);
        viewStatsButton.setPreferredSize(buttonSize);
        viewStatsButton.setBackground(new Color(144, 238, 144)); // LightGreen
        viewStatsButton.setForeground(Color.BLACK);
        viewStatsButton.setFocusPainted(false);
        viewStatsButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton manageReservationsButton = new JButton("Quản lý Hủy Đặt Bàn");
        manageReservationsButton.setFont(buttonFont);
        manageReservationsButton.setPreferredSize(buttonSize);
        manageReservationsButton.setBackground(new Color(144, 238, 144)); // LightGreen
        manageReservationsButton.setForeground(Color.BLACK);
        manageReservationsButton.setFocusPainted(false);
        manageReservationsButton.setBorder(BorderFactory.createRaisedBevelBorder());
        
        JButton viewTableStatusButton = new JButton("Xem Trạng Thái Bàn");
        viewTableStatusButton.setFont(buttonFont);
        viewTableStatusButton.setPreferredSize(buttonSize);
        viewTableStatusButton.setBackground(new Color(144, 238, 144)); // LightGreen
        viewTableStatusButton.setForeground(Color.BLACK);
        viewTableStatusButton.setFocusPainted(false);
        viewTableStatusButton.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton exitButton = new JButton("Thoát");
        exitButton.setFont(buttonFont);
        exitButton.setPreferredSize(buttonSize);
        exitButton.setBackground(new Color(255, 99, 71)); // Tomato
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createRaisedBevelBorder());

        // Panel để chứa các nút, căn giữa
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15)); // Center align buttons
        buttonPanel.setBackground(new Color(34, 139, 34)); // Forest Green
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding

        add(addDishButton);
        add(viewStatsButton);
        add(manageReservationsButton);
        add(viewTableStatusButton);
        add(exitButton);

        // Add action listeners
        addDishButton.addActionListener(e -> {
            AddMenuItemDialog dialog = new AddMenuItemDialog(this, "Thêm món ăn mới", true);
            dialog.setVisible(true);
        });

        viewStatsButton.addActionListener(e -> {
            StatisticsPage statsPage = new StatisticsPage();
            statsPage.setVisible(true);
        });

        // Action Listener for "Quản lý Hủy Đặt Bàn" button
        manageReservationsButton.addActionListener(e -> {
            // Pass the TableListPage instance directly as the refresh listener
            // Also, pass reservationPageInstance if AdminTableCancellationPage needs to refresh it too.
            // If AdminTableCancellationPage only refreshes tables, then just tableListPageInstance is fine.
            // Assuming AdminTableCancellationPage's constructor needs TableStatusRefreshListener
            // and reservationPageInstance also implements it:
            AdminTableCancellationPage adminCancelPage = new AdminTableCancellationPage(tableListPageInstance); // Passing TableListPage
            adminCancelPage.setVisible(true);
        });

        // Action Listener for "Xem Trạng Thái Bàn" button
        viewTableStatusButton.addActionListener(e -> {
            tableListPageInstance.setVisible(true); // Show the same instance of TableListPage
            tableListPageInstance.refreshTableStatus(); // Ensure it refreshes when opened
        });

        exitButton.addActionListener(e -> dispose());
    }

    // No-arg constructor for AdminPage if it's sometimes launched without a ReservationPage reference
    public AdminPage() {
        this(null); // Call the main constructor with a null ReservationPage instance
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing AdminPage in isolation, pass null or a dummy ReservationPage
            new AdminPage(null).setVisible(true);
        });
    }
}