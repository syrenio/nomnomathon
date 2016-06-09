package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.apache.camel.language.Simple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderRepository;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

@Component
public class PrepareForCreditCheckBean {

    @Autowired
    OrderRepository orderRepository;

    public void prepare(Exchange exchange, @Simple("header.orderId") Long orderId){
        OrderInProcess order = orderRepository.findOne(orderId);
        exchange.getIn().setHeader("creditCard", order.getCustomer().getCreditCard());
        /*amount already set in previous step*/
    }
}
