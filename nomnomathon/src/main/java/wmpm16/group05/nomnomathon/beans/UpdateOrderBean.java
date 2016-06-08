package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderRepository;

@Component
public class UpdateOrderBean {

    @Autowired
    OrderRepository orderRepository;

    public void update(Exchange exchange){
        OrderInProcess order = exchange.getIn().getBody(OrderInProcess.class);

        order = orderRepository.save(order);

        exchange.getOut().setBody(order);
    }
}
