package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.*;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;

import java.util.Map;

@Component
public class UpdateOrderBean {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    DishRepository dishRepository;

    public void update(Exchange exchange){
        OrderInProcess order = exchange.getIn().getBody(OrderInProcess.class);
        order = updateOrderFields(order, exchange);

        dishRepository.save(order.getDishes());
        order = orderRepository.save(order);

        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setBody(order);

        
    }

    /*
     * Update prices, restaurantId, state
     */
    private OrderInProcess updateOrderFields(OrderInProcess order, Exchange exchange){
        order.setRestaurantId(exchange.getIn().getHeader(NomNomConstants.HEADER_RESTAURANT_ID, Long.class));
        order.setState(exchange.getIn().getHeader(NomNomConstants.HEADER_ORDER_STATE, OrderState.class));
        /*set prices for dishes*/
        Map<String, Double> dishPrices = exchange.getIn().getHeader(NomNomConstants.HEADER_DISHES_PRICES, Map.class);
        /*add dish to order in case of random dish*/
        if (order.getDishes().size() == 0) {
            order.addDish(exchange.getIn().getHeader(NomNomConstants.HEADER_DISHES_ORDER, String.class));
        }
        if(dishPrices != null && dishPrices.size() > 0){
            for (Dish dish : order.getDishes()) {
                Double price = dishPrices.getOrDefault(dish.getDish(), 0d);
                dish.setPrice(price);
            }
        }

        return order;
    }
}
