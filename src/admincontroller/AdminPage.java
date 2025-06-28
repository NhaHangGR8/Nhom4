// AdminPage.java
package admincontroller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import restaurantmanagement.ReservationPage;
import restaurantmanagement.TableStatusRefreshListener;
import admincontroller.PaymentManagementPage;
import restaurantmanagement.Main; // Import Main class for window ancestor

// AdminPage should extend JPanel, not JFrame
public class AdminPage extends JPanel { // Changed from JFrame to JPanel

    private ReservationPage reservationPageInstance;
    private TableListPage tableListPageInstance;

    // Constructor to accept ReservationPage and TableListPage instances from Main
    public AdminPage(ReservationPage reservationPage, TableListPage tableListPage) {
        this.reservationPageInstance = reservationPage;
        this.tableListPageInstance = tableListPage;

        // Removed JFrame specific settings: setTitle, setSize, setLocationRelativeTo, setDefaultCloseOperation
        setLayout(new GridLayout(8, 1, 15, 15)); // Thay đổi GridLayout thành 8 hàng, 1 cột để thêm các nút mới
        setBackground(new Color(240, 240, 240)); // Set a background for the panel
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

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

        // Thêm các nút vào JPanel
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
            // Use SwingUtilities.getWindowAncestor(this) to get the parent Frame for dialogs
            AddMenuItemDialog dialog = new AddMenuItemDialog((JFrame) (Frame) SwingUtilities.getWindowAncestor(this), "Thêm món ăn mới", true);
            dialog.setVisible(true);
        });

        editDishButton.addActionListener(e -> {
            EditDishDialog dialog = new EditDishDialog((JFrame) (Frame) SwingUtilities.getWindowAncestor(this), "Sửa món ăn", true);
            dialog.setVisible(true);
        });

        deleteDishButton.addActionListener(e -> {
            DeleteDishDialog dialog = new DeleteDishDialog((JFrame) (Frame) SwingUtilities.getWindowAncestor(this), "Xóa món ăn", true);
            dialog.setVisible(true);
        });

        viewStatsButton.addActionListener(e -> {
            StatisticsPage statsPage = new StatisticsPage();
            statsPage.setVisible(true);
        });

        paymentBtn.addActionListener(e -> {
            // PaymentManagementPage still needs tableListPageInstance for refreshing
            PaymentManagementPage paymentPage = new PaymentManagementPage(tableListPageInstance);
            paymentPage.setVisible(true);
        });

        manageReservationsButton.addActionListener(e -> {
            // AdminTableCancellationPage also needs tableListPageInstance for refreshing
            AdminTableCancellationPage adminCancelPage = new AdminTableCancellationPage(tableListPageInstance);
            adminCancelPage.setVisible(true);
        });

        viewTableStatusButton.addActionListener(e -> {
            // Show the TableListPage and ensure it refreshes
            if (tableListPageInstance != null) {
                tableListPageInstance.refreshTableStatus(); // Refresh before showing
                tableListPageInstance.setVisible(true);
            }
        });

        exitButton.addActionListener(e -> {
            // Assuming "Thoát" means to go back to the previous panel in Main.
            // We can explicitly tell Main to show the Home panel or previous.
            Component ancestor = SwingUtilities.getWindowAncestor(this);
            if (ancestor instanceof Main) {
                ((Main) ancestor).showPanel("Home"); // Switch back to Home page
            }
            // If not running within Main, this JPanel doesn't need to do anything
            // as it's not a top-level window to dispose.
        });
    }

    // Default constructor if no ReservationPage or TableListPage is explicitly passed
    // It's generally better to make sure Main always passes these,
    // but this can be kept for backward compatibility if needed in other parts of the code.
    public AdminPage() {
        // This constructor might be called if AdminPage is created without specific instances
        // In a well-structured app, Main should manage and pass these instances.
        // For now, if this is called, it might lead to issues if TableListPage is not initialized.
        // This should ideally be removed if Main always provides the instances.
        // If this is used, consider how tableListPageInstance will be set.
        // For simplicity and to avoid NullPointer, we can initialize it here,
        // but it will create a new instance, potentially leading to inconsistent data.
        this(null, new TableListPage()); // Creates a new TableListPage if not passed.
    }
}