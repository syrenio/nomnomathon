package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.Order;

import java.util.List;
import java.util.Optional;

/**
 * Created by Agnes on 21.05.16.
 */
@Component
public class ExtractDishRestaurantBean {

    public void process(Exchange exchange){
        Order order = exchange.getIn().getBody(Order.class);
        Optional<Long> restaurant = order.getRestaurantid();
        List<Dish> dishes = order.getDishes();
    }
}
