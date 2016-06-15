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


        /*FIXME BAD Design, should be able to save this with the orderRepository.save(order) call, cascading db stuff*/
        dishRepository.save(order.getDishes());

        order = orderRepository.save(order);

        exchange.getOut().setBody(order);
    }

    /*
     * Update prices, restaurantId, status
     */
    private OrderInProcess updateOrderFields(OrderInProcess order, Exchange exchange){
        order.setRestaurantId(exchange.getIn().getHeader(NomNomConstants.HEADER_RESTAURANT_ID, Long.class));
        order.setState(OrderState.FULLFILLED);
        /*set prices for dishes*/
        Map<String, Double> dishPrices = exchange.getIn().getHeader(NomNomConstants.HEADER_DISHES_PRICES, Map.class);
        /*add dish to order in case of random dish*/
        if (order.getDishes().size() == 0) {
            order.addDish(exchange.getIn().getHeader(NomNomConstants.HEADER_DISHES_ORDER, String.class));
        }
        for (Dish dish : order.getDishes()) {
            Double price = dishPrices.getOrDefault(dish.getDish(), 0d);
            dish.setPrice(price);
        }

        return order;
    }
}
