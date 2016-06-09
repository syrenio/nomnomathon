package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.DishRepository;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderRepository;

@Component
public class UpdateOrderBean {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    DishRepository dishRepository;

    public void update(Exchange exchange){
        OrderInProcess order = exchange.getIn().getBody(OrderInProcess.class);

        /*FIXME BAD Design, should be able to save this with the orderRepository.save(order) call, cascading db stuff*/
        dishRepository.save(order.getDishes());

        order = orderRepository.save(order);

        exchange.getOut().setBody(order);
    }
}
