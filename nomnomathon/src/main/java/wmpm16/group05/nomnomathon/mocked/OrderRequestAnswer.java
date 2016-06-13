package wmpm16.group05.nomnomathon.mocked;

public class OrderRequestAnswer {
	
	@Override
	public String toString() {
		return "OrderRequestAnswer [accepted=" + accepted + ", restaurantId=" + restaurantId + ", orderId=" + orderId + "]";
	}
	
	 public OrderRequestAnswer() {
	    }
	
	public OrderRequestAnswer(Boolean accepted, Long restaurantId, Long orderId) {
		super();
		this.accepted = accepted;
		this.restaurantId = restaurantId;
		this.orderId = orderId;
	}
	private Boolean accepted;
	private Long restaurantId;
	private Long orderId;
	
	public Boolean isAccepted() {
		return accepted;
	}
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
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