package menu;

public class Dish {
    private int id; // Added ID for database operations
    private String name;
    private String description;
    private double price;
    private String imagePath;
    private String category;

    // Constructor for loading from DB (with ID)
    public Dish(int id, String name, String description, double price, String imagePath, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
    }

    // Constructor for new dishes (without ID, ID will be auto-generated by DB)
    public Dish(String name, String description, double price, String imagePath, String category) {
        this(-1, name, description, price, imagePath, category); // Call the main constructor with a default ID
    }

    // Constructor for MenuPage's existing usage (without category)
    public Dish(String name, String description, double price, String imagePath) {
        this(-1, name, description, price, imagePath, null); // Call main constructor
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategory() {
        return category;
    }

    // Setters (if needed)
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name + " - " + String.format("%,.0f", price) + " VNĐ";
    }
}