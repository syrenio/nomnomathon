package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.apache.camel.language.Simple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderInProcessRepository;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

@Component
public class PrepareForCreditCheckBean {

    @Autowired
    OrderInProcessRepository orderRepository;

    public void prepare(Exchange exchange, @Simple("header." + NomNomConstants.HEADER_ORDER_ID) Long orderId){
        OrderInProcess order = orderRepository.findOne(orderId);
        exchange.getIn().setHeader(NomNomConstants.HEADER_CREDIT_CARD, order.getCustomer().getCreditCard());
        /*amount already set in previous step*/
    }
}
