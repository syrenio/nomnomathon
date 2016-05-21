package wmpm16.group05.nomnomathon.models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OrderListEntry {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private long orderId;
    private long customerId;
    private String Restaurant;
    private String dish;
    private long dishId;
    private double quantity;
    private double price;
    private OrderState state;
    private Timestamp lastChange;
    
	
	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getRestaurant() {
		return Restaurant;
	}

	public void setRestaurant(String restaurant) {
		Restaurant = restaurant;
	}

	public String getDish() {
		return dish;
	}

	public void setDish(String dish) {
		this.dish = dish;
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

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public Timestamp getLastChange() {
		return lastChange;
	}

	public void setLastChange(Timestamp lastChange) {
		this.lastChange = lastChange;
	}

	@Override
	public String toString() {
		return "OrderListEntry [id=" + id + ", orderId=" + orderId + ", customerId=" + customerId + ", Restaurant="
				+ Restaurant + ", dish=" + dish + ", dishId=" + dishId + ", quantity=" + quantity + ", price=" + price
				+ ", state=" + state + ", lastChange=" + lastChange + "]";
	}
	
}
