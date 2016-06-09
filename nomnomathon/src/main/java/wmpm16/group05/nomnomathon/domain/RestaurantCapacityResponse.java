package wmpm16.group05.nomnomathon.domain;

public class RestaurantCapacityResponse {

    private boolean capacityAvailable;
    private Long restaurantId;
    private Long requestId;

    public RestaurantCapacityResponse() {
        super();
    }

    public RestaurantCapacityResponse(Long restaurantId, Long requestId) {
        super();
        this.restaurantId = restaurantId;
        this.requestId = requestId;
    }

    public boolean isCapacityAvailable() {
        return capacityAvailable;
    }

    public void setCapacityAvailable(boolean capacityAvailable) {
        this.capacityAvailable = capacityAvailable;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "RestaurantCapacityResponse [capacityAvailable=" + capacityAvailable + ", restaurantId=" + restaurantId
                + ", requestId=" + requestId + "]";
    }
}
