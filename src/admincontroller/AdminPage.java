package admincontroller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import restaurantmanagement.ReservationPage;
import restaurantmanagement.TableStatusRefreshListener; // Import the interface
import admincontroller.PaymentManagementPage;

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
        setSize(450, 550); // Tăng kích thước để phù hợp với các nút mới
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8, 1, 15, 15)); // Thay đổi GridLayout thành 8 hàng, 1 cột để thêm các nút mới

        // Cấu hình các nút với style nhất quán
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Dimension buttonSize = new Dimension(250, 50); // Kích thước cố định cho nút

        // Nút Thêm món ăn
        JButton addDishButton = new JButton("Thêm món ăn");
        addDishButton.setFont(buttonFont);
        addDishButton.setPreferredSize(buttonSize);
        addDishButton.setBackground(new Color(144, 238, 144)); // LightGreen
        addDishButton.setForeground(Color.BLACK);
        addDishButton.setFocusPainted(false);
        addDishButton.setBorder(BorderFactory.createRaisedBevelBorder());

        // Nút Sửa món ăn
        JButton editDishButton = new JButton("Sửa món ăn");
        editDishButton.setFont(buttonFont);
        editDishButton.setPreferredSize(buttonSize);
        editDishButton.setBackground(new Color(255, 255, 153)); // Light Yellow
        editDishButton.setForeground(Color.BLACK);
        editDishButton.setFocusPainted(false);
        editDishButton.setBorder(BorderFactory.createRaisedBevelBorder());

        // Nút Xóa món ăn
        JButton deleteDishButton = new JButton("Xóa món ăn");
        deleteDishButton.setFont(buttonFont);
        deleteDishButton.setPreferredSize(buttonSize);
        deleteDishButton.setBackground(new Color(255, 153, 153)); // Light Red
        deleteDishButton.setForeground(Color.BLACK);
        deleteDishButton.setFocusPainted(false);
        deleteDishButton.setBorder(BorderFactory.createRaisedBevelBorder());

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

        JButton paymentBtn = new JButton("Quản lý Thanh toán");
        paymentBtn.setFont(buttonFont);
        paymentBtn.setPreferredSize(buttonSize);
        paymentBtn.setBackground(new Color(144, 238, 144)); // Green
        paymentBtn.setForeground(Color.BLACK);
        paymentBtn.setFocusPainted(false);
        paymentBtn.setBorder(BorderFactory.createRaisedBevelBorder());

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

        // Thêm các nút vào JFrame theo thứ tự mới
        add(addDishButton);
        add(editDishButton);
        add(deleteDishButton);
        add(viewStatsButton);
        add(manageReservationsButton);
        add(paymentBtn);
        add(viewTableStatusButton);
        add(exitButton);

        // Add action listeners
        addDishButton.addActionListener(e -> {
            AddMenuItemDialog dialog = new AddMenuItemDialog(this, "Thêm món ăn mới", true);
            dialog.setVisible(true);
            // Có thể thêm logic làm mới bảng món ăn nếu AdminPage có hiển thị danh sách món
        });

        editDishButton.addActionListener(e -> {
            EditDishDialog dialog = new EditDishDialog(this, "Sửa món ăn", true);
            dialog.setVisible(true);
            // Có thể thêm logic làm mới bảng món ăn nếu AdminPage có hiển thị danh sách món
        });

        deleteDishButton.addActionListener(e -> {
            DeleteDishDialog dialog = new DeleteDishDialog(this, "Xóa món ăn", true);
            dialog.setVisible(true);
            // Có thể thêm logic làm mới bảng món ăn nếu AdminPage có hiển thị danh sách món
        });

        viewStatsButton.addActionListener(e -> {
            StatisticsPage statsPage = new StatisticsPage();
            statsPage.setVisible(true);
        });

        // Pass tableListPageInstance to PaymentManagementPage
        paymentBtn.addActionListener(e -> {
            PaymentManagementPage paymentPage = new PaymentManagementPage(tableListPageInstance); // Pass the instance
            paymentPage.setVisible(true);
        });

        // Action Listener for "Quản lý Hủy Đặt Bàn" button
        manageReservationsButton.addActionListener(e -> {
            // Pass the TableListPage instance directly as the refresh listener
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