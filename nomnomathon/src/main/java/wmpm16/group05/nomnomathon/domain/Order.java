package wmpm16.group05.nomnomathon.domain;

import java.util.List;
import java.util.Optional;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Order {
	public enum State {
		created, enriched, restaurantselected, fullfilled
	}
	
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long orderid;
	private Long userid;
	private Optional<Long> restaurantid;
	private String receiverName;
	public String getReceiverName() {
		return receiverName;
	}


	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	private String adress;
	
	public String getAdress() {
		return adress;
	}


	public void setAdress(String adress) {
		this.adress = adress;
	}


	public State getState() {
		return state;
	}

	private List<Dish> dishes;
	
	private State state = State.created;
	
	public Order() {};
	

	public Order(Long userid, Optional<Long> restaurantid, List<Dish> dishes) {
		super();
		this.userid = userid;
		this.restaurantid = restaurantid;
		this.dishes = dishes;
	}

	@Override
	public String toString() {
		return "Order [orderid=" + orderid + ", userid=" + userid + ", restaurantid=" + restaurantid + ", receiverName="
				+ receiverName + ", adress=" + adress + ", dishes=" + dishes + ", state=" + state + "]";
	}
	
	public void proceed (State state) {
		//should check if the transition is valid.
		this.state = state;
	}


	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
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
	

}
