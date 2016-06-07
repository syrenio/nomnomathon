package wmpm16.group05.nomnomathon.mocked;

public class OrderRequestAnswer {
	
	public enum OrderRequestState {
		accepted, rejected;
	}
	
	
	
	public OrderRequestAnswer(OrderRequestState answer, Long restaurantId, Long orderId) {
		super();
		this.answer = answer;
		this.restaurantId = restaurantId;
		this.orderId = orderId;
	}
	private OrderRequestState answer;
	private Long restaurantId;
	private Long orderId;
	
	public OrderRequestState getAnswer() {
		return answer;
	}
	public void setAnswer(OrderRequestState answer) {
		this.answer = answer;
	}
	public Long getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(Long restaurantId) {
		this.restaurantId = restaurantId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	
}