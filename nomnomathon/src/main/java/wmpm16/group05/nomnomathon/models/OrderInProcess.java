package wmpm16.group05.nomnomathon.models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class OrderInProcess {

	@Id
	@GeneratedValue
	private Long orderId;
	private OrderState state;
	@ManyToOne
	private Customer customer;
	private Long restaurantId;
	@OneToMany(fetch = FetchType.EAGER)
	private List<Dish> dishes = new ArrayList<Dish>();



	@Override
	public String toString() {
		return "OrderInProcess [orderId=" + orderId + ", state=" + state + ", customer=" + customer + ", restaurantId="
				+ restaurantId + ", dishes=" + dishes + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderInProcess other = (OrderInProcess) obj;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (dishes == null) {
			if (other.dishes != null)
				return false;
		} else if (!dishes.equals(other.dishes))
			return false;
		if (orderId == null) {
			if (other.orderId != null)
				return false;
		} else if (!orderId.equals(other.orderId))
			return false;
		if (restaurantId == null) {
			if (other.restaurantId != null)
				return false;
		} else if (!restaurantId.equals(other.restaurantId))
			return false;
		return state == other.state;
	}

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

	public List<Dish> getDishes() {
		return dishes;
	}

	public void setDishes(List<Dish> dishes) {
		this.dishes = dishes;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setRestaurantId(Long restaurantid) {
		this.restaurantId = restaurantid;
	}
	
	public Long getRestaurantId() {
		return this.restaurantId;
	}

	public void proceed(OrderState state) {
		this.state = state;
	}

	public void addDish(String dishName) {
		Dish dish = new Dish();
		dish.setDish(dishName);
		this.dishes.add(dish);
	}

}
