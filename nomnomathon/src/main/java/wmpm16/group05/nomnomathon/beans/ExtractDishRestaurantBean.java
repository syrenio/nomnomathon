package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.OrderInProcess;

import java.util.List;

/**
 * Created by Agnes on 21.05.16.
 */
@Component
public class ExtractDishRestaurantBean {

    public void process(Exchange exchange){
        OrderInProcess order = exchange.getIn().getBody(OrderInProcess.class);
        List<Dish> dishes = order.getDishes();

        String dishNames = "";
        for (Dish d : dishes) {
            dishNames += "\"" + d.getDish() + "\", ";
        }
        dishNames = dishNames.substring(0, dishNames.length() - 2);

        exchange.getIn().setHeader("dishNames", dishNames);

    }
}
