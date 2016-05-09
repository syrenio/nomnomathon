package wmpm16.group05.nomnomathon.domain;

import java.util.List;

/**
 * Created by syrenio on 04/05/16.
 */
public class OrderRequest {
    private OrderType type;
    private String text;
    private Long phoneNumber;
    private Long userId;
    private List<Long> dishes;

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

    public List<Long> getDishes() {
        return dishes;
    }

    public void setDishes(List<Long> dishes) {
        this.dishes = dishes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
