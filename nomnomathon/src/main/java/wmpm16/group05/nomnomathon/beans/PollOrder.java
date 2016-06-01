package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.language.Simple;
import org.springframework.beans.factory.annotation.Autowired;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderRepository;


/**
 * Created by Agnes on 24.05.16.
 */
public class PollOrder {

    @Autowired
    OrderRepository orderRepository;

    public OrderInProcess findOrder(@Simple("header.orderId") Long orderId) {
        return orderRepository.findOne(orderId);
    }

}
