package restaurantmanagement;

public class DishOrder {
    private Dish dish;
    private int quantity;

    public DishOrder(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return dish.getName() + " x " + quantity + " (" + String.format("%,.0f", dish.getPrice() * quantity) + " VNĐ)";
    }
}