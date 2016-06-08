package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderRepository;

@Component
public class LoadOrderBean {

    @Autowired
    OrderRepository orderRepository;

    public void load(Exchange exchange){
        Long orderId = exchange.getIn().getHeader("orderId", Long.class);
        OrderInProcess order = orderRepository.findOne(orderId);
        exchange.getOut().setBody(order);
    }
}
