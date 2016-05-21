package wmpm16.group05.nomnomathon.models;

public class Dish {
    private String dishName;
    private long dishId;
    private double quantity;
    private double price;
    
	public String getDish() {
		return dishName;
	}
	
	public void setDish(String dish) {
		this.dishName = dish;
	}
	
	public long getDishId() {
		return dishId;
	}
	
	public void setDishId(long dishId) {
		this.dishId = dishId;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return "Dish [dishName=" + dishName + ", dishId=" + dishId + ", quantity=" + quantity + ", price=" + price
				+ "]";
	}

}
