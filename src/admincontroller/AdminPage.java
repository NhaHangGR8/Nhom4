// AdminPage.java
package admincontroller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import restaurantmanagement.ReservationPage;
import restaurantmanagement.TableStatusRefreshListener;
import admincontroller.PaymentManagementPage;
import restaurantmanagement.Main; // Import Main class for window ancestor

// Import for dish management and statistics dialogs/pages
import admincontroller.AddMenuItemDialog;
import admincontroller.DeleteDishDialog;
import admincontroller.EditDishDialog;
import admincontroller.StatisticsPage; // Assuming this is the correct class name for statistics

// AdminPage should extend JPanel, not JFrame
public class AdminPage extends JPanel {

    private ReservationPage reservationPageInstance;
    private TableListPage tableListPageInstance; // You need to ensure TableListPage is properly initialized.

    // Constructor to accept ReservationPage and TableListPage instances from Main
    public AdminPage(ReservationPage reservationPage, TableListPage tableListPage) {
        this.reservationPageInstance = reservationPage;
        this.tableListPageInstance = tableListPage;

        // Removed JFrame specific settings: setTitle, setSize, setLocationRelativeTo, setDefaultCloseOperation
        setLayout(new GridLayout(10, 1, 15, 15)); // Changed GridLayout to 10 rows, 1 column for new buttons
        setBackground(new Color(240, 240, 240)); // Set a background for the panel
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

        // Cấu hình các nút với style nhất quán
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Dimension buttonSize = new Dimension(250, 50); // Kích thước cố định cho nút

        // Nút Thêm món
        JButton addDishButton = new JButton("Thêm Món Ăn");
        addDishButton.setFont(buttonFont);
        addDishButton.setPreferredSize(buttonSize);
        addDishButton.setBackground(new Color(52, 152, 219)); // Blue
        addDishButton.setForeground(Color.WHITE);
        addDishButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddMenuItemDialog addDishDialog = new AddMenuItemDialog(parentFrame, "Thêm Món Ăn Mới", true);
            addDishDialog.setVisible(true);
            // Optionally, refresh a dish display table if AdminPage had one
        });
        add(addDishButton);

        // Nút Sửa món
        JButton editDishButton = new JButton("Sửa Món Ăn");
        editDishButton.setFont(buttonFont);
        editDishButton.setPreferredSize(buttonSize);
        editDishButton.setBackground(new Color(46, 204, 113)); // Green
        editDishButton.setForeground(Color.WHITE);
        editDishButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            EditDishDialog editDishDialog = new EditDishDialog(parentFrame, "Sửa Món Ăn", true);
            editDishDialog.setVisible(true);
            // Optionally, refresh a dish display table
        });
        add(editDishButton);

        // Nút Xóa món
        JButton deleteDishButton = new JButton("Xóa Món Ăn");
        deleteDishButton.setFont(buttonFont);
        deleteDishButton.setPreferredSize(buttonSize);
        deleteDishButton.setBackground(new Color(231, 76, 60)); // Red-ish
        deleteDishButton.setForeground(Color.WHITE);
        deleteDishButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            DeleteDishDialog deleteDishDialog = new DeleteDishDialog(parentFrame, "Xóa Món Ăn", true);
            deleteDishDialog.setVisible(true);
            // Optionally, refresh a dish display table
        });
        add(deleteDishButton);

        // Nút Quản lý bàn ăn (TableListPage)
        JButton manageTablesButton = new JButton("Quản lý Bàn Ăn");
        manageTablesButton.setFont(buttonFont);
        manageTablesButton.setPreferredSize(buttonSize);
        manageTablesButton.setBackground(new Color(155, 89, 182)); // Purple
        manageTablesButton.setForeground(Color.WHITE);
        manageTablesButton.addActionListener(e -> {
            if (tableListPageInstance != null) {
                tableListPageInstance.setVisible(true); // Open TableListPage JFrame
            } else {
                JOptionPane.showMessageDialog(this, "TableListPage chưa được khởi tạo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(manageTablesButton);

        // Nút Quản lý Thanh toán
        JButton managePaymentsButton = new JButton("Quản lý Thanh Toán");
        managePaymentsButton.setFont(buttonFont);
        managePaymentsButton.setPreferredSize(buttonSize);
        managePaymentsButton.setBackground(new Color(22, 160, 133)); // Dark Green
        managePaymentsButton.setForeground(Color.WHITE);
        managePaymentsButton.addActionListener(e -> {
            // PaymentManagementPage cần TableListPage để refresh trạng thái bàn
            PaymentManagementPage paymentPage = new PaymentManagementPage(tableListPageInstance);
            paymentPage.setVisible(true);
        });
        add(managePaymentsButton);

        // NÚT Quản lý Nhân viên (Integrated EmployeeManagementPage)
        JButton manageEmployeesButton = new JButton("Quản lý Nhân Viên");
        manageEmployeesButton.setFont(buttonFont);
        manageEmployeesButton.setPreferredSize(buttonSize);
        manageEmployeesButton.setBackground(new Color(230, 126, 34)); // Dark Orange
        manageEmployeesButton.setForeground(Color.WHITE);
        manageEmployeesButton.addActionListener(e -> {
            // Open EmployeeManagementPage when the button is clicked
            EmployeeManagementPage employeePage = new EmployeeManagementPage();
            employeePage.setVisible(true);
        });
        add(manageEmployeesButton);

        // Nút Báo cáo và Thống kê
        JButton reportsButton = new JButton("Báo cáo & Thống kê");
        reportsButton.setFont(buttonFont);
        reportsButton.setPreferredSize(buttonSize);
        reportsButton.setBackground(new Color(52, 73, 94)); // Dark Blue/Grey
        reportsButton.setForeground(Color.WHITE);
        reportsButton.addActionListener(e -> {
            // Mở StatisticsPage
            StatisticsPage statsPage = new StatisticsPage();
            statsPage.setVisible(true);
        });
        add(reportsButton);

        // Nút Đăng xuất
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(buttonFont);
        logoutButton.setPreferredSize(buttonSize);
        logoutButton.setBackground(new Color(192, 57, 43)); // Red
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            Component ancestor = SwingUtilities.getWindowAncestor(this);
            if (ancestor instanceof Main) {
                ((Main) ancestor).showPanel("Home"); // Switch back to Home page
                // Optionally, clear login session here if "Logout" means actual logout
                // LoginSession.logout();
            }
        });
        add(logoutButton);

        // Nút Thoát ứng dụng (mới)
        JButton exitApplicationButton = new JButton("Thoát Ứng Dụng");
        exitApplicationButton.setFont(buttonFont);
        exitApplicationButton.setPreferredSize(buttonSize);
        exitApplicationButton.setBackground(new Color(100, 100, 100)); // Dark Grey
        exitApplicationButton.setForeground(Color.WHITE);
        exitApplicationButton.addActionListener(e -> {
            // Thoát toàn bộ ứng dụng
            System.exit(0);
        });
        add(exitApplicationButton);
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