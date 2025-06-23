package restaurantmanagement;

import admincontroller.DishSelectionDialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import restaurantmanagement.LoginSession;

public class ReservationPage extends JPanel {
    private String titleText = "Đặt Bàn Tại Gr8 Restaurant";
    private String headerText = "Chọn bàn bạn muốn đặt và điền thông tin";

    // Form Fields (Đặt bàn mới)
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField dateField; // dd/MM/yyyy
    private JTextField timeField; // HH:mm
    private JSpinner guestsSpinner;
    private JTextArea requestsArea;
    private JLabel statusLabel;

    // Table Selection Components
    private JPanel tableSelectionPanel; // Main panel for table layout
    private JPanel outdoorTablePanel;
    private JPanel indoorTablePanel;
    private Map<Integer, Table> allTables; // Map to store all table objects by ID
    private Map<JButton, Table> tableButtonMap; // Map to link buttons to Table objects
    private JButton selectedTableButton = null; // The currently selected table button
    private int selectedTableId = -1; // The ID of the currently selected table
    private JLabel selectedTableInfoLabel; // Displays info about the selected table

    // Dish Selection Components
    private JButton selectDishesButton;
    private JTextArea selectedDishesArea;
    private List<DishOrder> currentSelectedDishes; // To store selected dishes and quantities

    private JButton reserveButton; // Nút xác nhận đặt bàn

    public ReservationPage() {
        currentSelectedDishes = new ArrayList<>();
        allTables = new HashMap<>();
        tableButtonMap = new HashMap<>();
        setupUI();
        loadTablesFromDatabase(); // Load tables (or initialize if DB is empty)
        enableReservationForm(false); // Disable form initially
    }

    private void setupUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(250, 255, 255));

        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(41, 128, 185));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel that holds table selection and reservation form
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(getBackground());
        contentPanel.add(Box.createVerticalStrut(10));

        // Header for table selection
        JLabel headerLabel = new JLabel(headerText, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setForeground(new Color(80, 80, 80));
        contentPanel.add(headerLabel);
        contentPanel.add(Box.createVerticalStrut(25));

        // --- Table Selection Section ---
        setupTableSelectionUI(); // Setup the table selection panels
        contentPanel.add(tableSelectionPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // --- Reservation Form Section ---
        JPanel reservationFormPanel = createReservationFormPanel();
        contentPanel.add(reservationFormPanel);
        contentPanel.add(Box.createVerticalGlue()); // Pushes content to top

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTableSelectionUI() {
        tableSelectionPanel = new JPanel();
        tableSelectionPanel.setLayout(new BoxLayout(tableSelectionPanel, BoxLayout.Y_AXIS));
        tableSelectionPanel.setBackground(getBackground());
        tableSelectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(170, 170, 170)), "Chọn Bàn",
            javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 20), new Color(41, 128, 185)));

        // Selected table info label
        selectedTableInfoLabel = new JLabel("Chưa có bàn nào được chọn.", SwingConstants.CENTER);
        selectedTableInfoLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        selectedTableInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedTableInfoLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        tableSelectionPanel.add(selectedTableInfoLabel);

        // Outdoor Tables
        // Sử dụng GridLayout để các nút bàn tự chia sẻ không gian ngang và dọc
        outdoorTablePanel = new JPanel(new GridLayout(0, 5, 15, 15)); // 0 hàng, 5 cột, khoảng cách 15x15
        outdoorTablePanel.setBackground(getBackground());
        outdoorTablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Bàn Ngoài Trời",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16), new Color(60, 60, 60)));

        // Indoor Tables
        // Sử dụng GridLayout tương tự
        indoorTablePanel = new JPanel(new GridLayout(0, 5, 15, 15)); // 0 hàng, 5 cột, khoảng cách 15x15
        indoorTablePanel.setBackground(getBackground());
        indoorTablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Bàn Trong Nhà",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16), new Color(60, 60, 60)));

        tableSelectionPanel.add(outdoorTablePanel);
        tableSelectionPanel.add(indoorTablePanel);
    }

    private void loadTablesFromDatabase() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseHelper.getConnection();
            String createTableSql = "CREATE TABLE IF NOT EXISTS tables (" +
                                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                                    "capacity INTEGER NOT NULL," +
                                    "location VARCHAR(50) NOT NULL," +
                                    "status VARCHAR(50) NOT NULL DEFAULT 'available'" +
                                    ")";
            stmt = conn.createStatement();
            stmt.execute(createTableSql);

            rs = stmt.executeQuery("SELECT COUNT(*) FROM tables");
            if (rs.next() && rs.getInt(1) == 0) {
                initializeDefaultTables(conn);
            }

            String selectSql = "SELECT id, capacity, location, status FROM tables ORDER BY id";
            rs = stmt.executeQuery(selectSql);
            allTables.clear();
            outdoorTablePanel.removeAll();
            indoorTablePanel.removeAll();

            while (rs.next()) {
                int id = rs.getInt("id");
                int capacity = rs.getInt("capacity");
                String location = rs.getString("location");
                String status = rs.getString("status");
                Table table = new Table(id, capacity, location, status);
                allTables.put(id, table);
                JButton tableButton = createTableButton(table);
                tableButtonMap.put(tableButton, table);

                if ("outdoor".equalsIgnoreCase(location)) {
                    outdoorTablePanel.add(tableButton);
                } else {
                    indoorTablePanel.add(tableButton);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải hoặc khởi tạo dữ liệu bàn: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
        }
        revalidate();
        repaint();
    }

    private void initializeDefaultTables(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        String insertSql = "INSERT INTO tables (capacity, location, status) VALUES (?, ?, ?)";
        try {
            pstmt = conn.prepareStatement(insertSql);
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(1, 4);
                pstmt.setString(2, "outdoor");
                pstmt.setString(3, "available");
                pstmt.addBatch();
            }
            for (int i = 0; i < 10; i++) {
                pstmt.setInt(1, 4);
                pstmt.setString(2, "indoor");
                pstmt.setString(3, "available");
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private JButton createTableButton(Table table) {
        JButton button = new JButton("<html>Bàn " + table.getId() + "<br>(" + table.getCapacity() + " chỗ)<br>" + 
                                       (table.getLocation().equals("outdoor") ? "Ngoài trời" : "Trong nhà") + "</html>");
        // BỎ DÒNG SAU để các nút bàn tự điều chỉnh kích thước theo GridLayout
        // button.setPreferredSize(new Dimension(110, 90)); 
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setLayout(new BorderLayout());
        
        updateTableButtonUI(button, table);

        button.addActionListener(e -> {
            if ("available".equalsIgnoreCase(table.getStatus())) {
                if (selectedTableButton != null && selectedTableButton != button) {
                    Table prevSelectedTable = tableButtonMap.get(selectedTableButton);
                    updateTableButtonUI(selectedTableButton, prevSelectedTable);
                }
                
                selectedTableButton = button;
                selectedTableId = table.getId();
                selectedTableInfoLabel.setText("Đã chọn: Bàn " + table.getId() + " (" + table.getCapacity() + " chỗ, " + (table.getLocation().equals("outdoor") ? "Ngoài trời" : "Trong nhà") + ")");
                
                selectedTableButton.setBackground(new Color(255, 204, 102));
                selectedTableButton.setBorder(BorderFactory.createLineBorder(new Color(204, 102, 0), 3));
                
                enableReservationForm(true);
            } else {
                JOptionPane.showMessageDialog(this, "Bàn " + table.getId() + " hiện không khả dụng. Trạng thái: " + table.getStatus(), "Thông báo", JOptionPane.WARNING_MESSAGE);
                enableReservationForm(false);
                selectedTableId = -1;
                if (selectedTableButton != null) {
                    Table prevSelectedTable = tableButtonMap.get(selectedTableButton);
                    updateTableButtonUI(selectedTableButton, prevSelectedTable);
                    selectedTableButton = null;
                }
                selectedTableInfoLabel.setText("Chưa có bàn nào được chọn.");
            }
        });
        return button;
    }

    private void updateTableButtonUI(JButton button, Table table) {
        button.setForeground(Color.BLACK);
        Color bgColor;
        switch (table.getStatus().toLowerCase()) {
            case "available":
                bgColor = new Color(144, 238, 144);
                break;
            case "reserved":
                bgColor = new Color(255, 165, 0);
                break;
            case "occupied":
                bgColor = new Color(220, 20, 60);
                button.setForeground(Color.WHITE);
                break;
            case "out_of_service":
                bgColor = new Color(192, 192, 192);
                break;
            default:
                bgColor = Color.LIGHT_GRAY;
        }
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        button.setFocusPainted(false);
    }

    private JPanel createReservationFormPanel() {
        JPanel reservationFormPanel = new JPanel(new GridBagLayout());
        reservationFormPanel.setBackground(getBackground());
        reservationFormPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(170, 170, 170)), "Thông Tin Đặt Bàn",
            javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 20), new Color(41, 128, 185)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally by default

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; reservationFormPanel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; // Allow text field to expand horizontally
        nameField = new JTextField(20); reservationFormPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; reservationFormPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        emailField = new JTextField(20); reservationFormPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; reservationFormPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        phoneField = new JTextField(20); reservationFormPanel.add(phoneField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; reservationFormPanel.add(new JLabel("Ngày đặt bàn (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        dateField = new JTextField(15);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        reservationFormPanel.add(dateField, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0; reservationFormPanel.add(new JLabel("Giờ đặt bàn (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        timeField = new JTextField(15);
        timeField.setText("19:00");
        reservationFormPanel.add(timeField, gbc);

        // Guests
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0; reservationFormPanel.add(new JLabel("Số lượng khách:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; // Allow spinner to expand horizontally too
        SpinnerNumberModel guestsModel = new SpinnerNumberModel(1, 1, 20, 1);
        guestsSpinner = new JSpinner(guestsModel);
        reservationFormPanel.add(guestsSpinner, gbc);

        // --- Dish Selection Section ---
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.weightx = 1.0; // Button can expand
        selectDishesButton = new JButton("Chọn Món Ăn");
        selectDishesButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectDishesButton.setBackground(new Color(52, 152, 219));
        selectDishesButton.setForeground(Color.WHITE);
        selectDishesButton.setFocusPainted(false);
        selectDishesButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        selectDishesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DishSelectionDialog dishDialog = new DishSelectionDialog((JFrame) SwingUtilities.getWindowAncestor(ReservationPage.this));
                dishDialog.setVisible(true);
                currentSelectedDishes = dishDialog.getSelectedDishes();
                updateSelectedDishesArea();
            }
        });
        reservationFormPanel.add(selectDishesButton, gbc);

        gbc.gridy = 7; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; // Allow JScrollPane to expand both H and V
        selectedDishesArea = new JTextArea(5, 20);
        selectedDishesArea.setLineWrap(true);
        selectedDishesArea.setWrapStyleWord(true);
        selectedDishesArea.setEditable(false);
        JScrollPane dishesScrollPane = new JScrollPane(selectedDishesArea);
        reservationFormPanel.add(dishesScrollPane, gbc);

        // Special Requests
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 0.0; gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill for label
        reservationFormPanel.add(new JLabel("Yêu cầu đặc biệt:"), gbc);
        gbc.gridy = 9; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; // Allow JScrollPane to expand both H and V
        requestsArea = new JTextArea(4, 20);
        requestsArea.setLineWrap(true);
        requestsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(requestsArea);
        reservationFormPanel.add(scrollPane, gbc);

        // Status Label
        gbc.gridy = 10; gbc.weighty = 0.0; gbc.fill = GridBagConstraints.HORIZONTAL; // Reset weighty
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        reservationFormPanel.add(statusLabel, gbc);

        // Reserve Button
        gbc.gridy = 11;
        reserveButton = new JButton("Xác Nhận Đặt Bàn");
        reserveButton.setFont(new Font("Arial", Font.BOLD, 18));
        reserveButton.setBackground(new Color(46, 204, 113));
        reserveButton.setForeground(Color.WHITE);
        reserveButton.setFocusPainted(false);
        reserveButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitReservation();
            }
        });
        reservationFormPanel.add(reserveButton, gbc);

        return reservationFormPanel;
    }

    private void enableReservationForm(boolean enable) {
        nameField.setEnabled(enable);
        emailField.setEnabled(enable);
        phoneField.setEnabled(enable);
        dateField.setEnabled(enable);
        timeField.setEnabled(enable);
        guestsSpinner.setEnabled(enable);
        requestsArea.setEnabled(enable);
        selectDishesButton.setEnabled(enable);
        reserveButton.setEnabled(enable);
    }

    private void updateSelectedDishesArea() {
        StringBuilder sb = new StringBuilder("Các món đã chọn:\n");
        if (currentSelectedDishes.isEmpty()) {
            sb.append("Chưa có món nào được chọn.");
        } else {
            for (DishOrder order : currentSelectedDishes) {
                sb.append("- ").append(order.toString()).append("\n");
            }
        }
        selectedDishesArea.setText(sb.toString());
    }

    private void submitReservation() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dateText = dateField.getText().trim();
        String timeText = timeField.getText().trim();
        int guests = (Integer) guestsSpinner.getValue();
        String requests = requestsArea.getText().trim();

        if (selectedTableId == -1) {
            statusLabel.setText("Vui lòng chọn một bàn.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || dateText.isEmpty() || timeText.isEmpty()) {
            statusLabel.setText("Vui lòng điền đầy đủ thông tin.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        LocalDate reservationDate;
        LocalTime reservationTime;
        try {
            reservationDate = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            reservationTime = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException ex) {
            statusLabel.setText("Ngày hoặc giờ không hợp lệ. Định dạng: dd/MM/yyyy và HH:mm");
            statusLabel.setForeground(Color.RED);
            return;
        }

        StringBuilder fullRequests = new StringBuilder(requests);
        if (!currentSelectedDishes.isEmpty()) {
            if (!requests.isEmpty()) {
                fullRequests.append("\n\n");
            }
            fullRequests.append("Món đã đặt:\n");
            for (DishOrder order : currentSelectedDishes) {
                fullRequests.append("- ").append(order.getDish().getName()).append(" x ").append(order.getQuantity()).append("\n");
            }
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseHelper.getConnection();
            
            // --- Start of integrated code from your request ---
            // Kiểm tra trạng thái bàn
            String checkSql = "SELECT status FROM tables WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, selectedTableId); // Use selectedTableId
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                statusLabel.setText("Không tìm thấy bàn đã chọn."); // Adapted message
                statusLabel.setForeground(Color.RED);
                return;
            }

            String tableStatus = rs.getString("status"); // Use tableStatus to avoid conflict with existing 'status'
            if (!"available".equalsIgnoreCase(tableStatus)) { // Use tableStatus
                statusLabel.setText("Bàn " + selectedTableId + " hiện không khả dụng. Trạng thái: " + tableStatus); // Adapted message
                statusLabel.setForeground(Color.RED);
                return;
            }
            rs.close();
            checkStmt.close();
            // --- End of table status check ---

            // Reuse the isTableAvailable logic for time conflict check
            if (!isTableAvailable(conn, selectedTableId, reservationDate, reservationTime)) {
                statusLabel.setText("Bàn " + selectedTableId + " không khả dụng vào thời gian này.");
                statusLabel.setForeground(Color.RED);
                return;
            }

            // Thêm vào bảng reservations
            String insertSql = """
                INSERT INTO reservations (
                    customer_name, customer_email, customer_phone,
                    reservation_date, reservation_time, number_of_guests,
                    special_requests, table_id, status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'confirmed')
            """; // Changed status to 'confirmed' to match existing logic
            pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name); // Use 'name'
            pstmt.setString(2, email); // Use 'email'
            pstmt.setString(3, phone); // Use 'phone'
            pstmt.setDate(4, Date.valueOf(reservationDate)); // Use 'reservationDate'
            pstmt.setTime(5, Time.valueOf(reservationTime)); // Use 'reservationTime'
            pstmt.setInt(6, guests); // Use 'guests'
            pstmt.setString(7, fullRequests.toString()); // Use 'fullRequests'
            pstmt.setInt(8, selectedTableId); // Use 'selectedTableId'
            
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                int reservationId = -1;
                if (generatedKeys.next()) {
                    reservationId = generatedKeys.getInt(1);
                }
                generatedKeys.close();

                // Cập nhật trạng thái bàn
                // This logic is already present, ensuring consistency
                Table currentTable = allTables.get(selectedTableId);
                if (currentTable != null) {
                    currentTable.status = "reserved";
                    updateTableStatus(conn, selectedTableId, "reserved");
                    updateTableButtonUI(selectedTableButton, currentTable);
                }

                statusLabel.setText("Đặt bàn thành công! ID đặt bàn: " + reservationId);
                statusLabel.setForeground(new Color(34, 139, 34));
                JOptionPane.showMessageDialog(this, "Đặt bàn thành công! ID đặt bàn của bạn là: " + reservationId, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                enableReservationForm(false);
            } else {
                statusLabel.setText("Đặt bàn thất bại. Vui lòng thử lại.");
                statusLabel.setForeground(Color.RED);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Lỗi CSDL: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        } finally {
            DatabaseHelper.closeConnection(conn);
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private boolean isTableAvailable(Connection conn, int tableId, LocalDate date, LocalTime time) throws SQLException {
        // This method now only checks for time conflicts based on existing reservations.
        // The overall table status check ("out_of_service", "occupied") is handled at the beginning of submitReservation.
        String sql = "SELECT COUNT(*) FROM reservations WHERE table_id = ? AND reservation_date = ? AND reservation_time = ? AND status IN ('confirmed', 'pending')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tableId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.setTime(3, java.sql.Time.valueOf(time));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        }
        return true;
    }

    private void updateTableStatus(Connection conn, int tableId, String status) throws SQLException {
        String sql = "UPDATE tables SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, tableId);
            pstmt.executeUpdate();
        }
    }

    private void resetForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        timeField.setText("19:00");
        guestsSpinner.setValue(1);
        requestsArea.setText("");
        selectedDishesArea.setText("Chưa có món nào được chọn.");
        currentSelectedDishes.clear();
        
        if (selectedTableButton != null) {
            Table prevSelectedTable = tableButtonMap.get(selectedTableButton);
            if (prevSelectedTable != null) { 
                updateTableButtonUI(selectedTableButton, prevSelectedTable);
            }
            selectedTableButton = null;
        }
        selectedTableId = -1; 
        selectedTableInfoLabel.setText("Chưa có bàn nào được chọn.");
    }
    
    private static class Table {
        int id;
        int capacity;
        String location;
        String status;

        public Table(int id, int capacity, String location, String status) {
            this.id = id;
            this.capacity = capacity;
            this.location = location;
            this.status = status;
        }
        
        public int getId() { return id; }
        public int getCapacity() { return capacity; }
        public String getLocation() { return location; }
        public String getStatus() { return status; }
    }
}