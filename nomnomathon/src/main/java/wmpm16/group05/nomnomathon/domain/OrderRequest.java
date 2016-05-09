package wmpm16.group05.nomnomathon.domain;

import java.util.List;
import java.util.Optional;

/**
 * Created by syrenio on 04/05/16.
 */
public class OrderRequest {
    private OrderType type;
    private String text;
    private Long phoneNumber;
    private Optional<Long> userId;
    private List<String> dishes;
    private Optional<Long> restaurantId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getDishes() {
        return dishes;
    }

    public void setDishes(List<String> dishes) {
        this.dishes = dishes;
    }

    public Optional<Long> getUserId() {
        return userId;
    }

    public void setUserId(Optional<Long> userId) {
        this.userId = userId;
    }

    public Optional<Long> getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Optional<Long> restaurantId) {
        this.restaurantId = restaurantId;
    }
}
