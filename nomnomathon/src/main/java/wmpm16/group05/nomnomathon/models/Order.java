package wmpm16.group05.nomnomathon.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Order {
	
	private Long orderId;
	private OrderState state;
	private Customer customer;
	private Optional<Long> restaurantid;
	private List<Dish> dishes = new ArrayList<Dish>();

	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Optional<Long> getRestaurantid() {
		return restaurantid;
	}

	public void setRestaurantid(Optional<Long> restaurantid) {
		this.restaurantid = restaurantid;
	}

	public List<Dish> getDishes() {
		return dishes;
	}

	public void setDishes(List<Dish> dishes) {
		this.dishes = dishes;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", state=" + state + ", customer=" + customer + ", restaurantid="
				+ restaurantid + ", dishes=" + dishes + "]";
	}

	public void proceed (OrderState state) {
		// TODO should check if the transition is valid or switch to next state
		this.state = state;
	}

	public void addDish(String dishName) {
		Dish dish = new Dish();
		dish.setDish(dishName);
		this.dishes.add(dish);
	}
	
}
