package restaurantmanagement;

// Lớp này quản lý trạng thái đăng nhập của người dùng.
public class LoginSession {

    private static boolean loggedIn = false;
    private static String currentUserRole = null; // Ví dụ: "customer", "admin", "staff"
    private static String currentUsername = null;

    public static void setLoggedInUser(String username, String role) {
        loggedIn = true;
        currentUsername = username;
        currentUserRole = role;
    }

    public static void logout() {
        loggedIn = false;
        currentUsername = null;
        currentUserRole = null;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static boolean isAdmin() {
        return loggedIn && "admin".equalsIgnoreCase(currentUserRole);
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    public static String getLoggedInUserRole() { // Changed to public static String
        return currentUserRole;
    }
}