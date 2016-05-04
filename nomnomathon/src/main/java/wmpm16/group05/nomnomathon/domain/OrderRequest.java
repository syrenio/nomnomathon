package wmpm16.group05.nomnomathon.domain;

/**
 * Created by syrenio on 04/05/16.
 */
public class OrderRequest {
    private OrderType type;
    private String text;

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
}
