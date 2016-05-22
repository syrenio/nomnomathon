package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;

import java.util.Optional;

/**
 * Created by syrenio on 5/20/2016.
 */
@Component
public class SMSAuthBean {
    @Autowired
    private CustomerRepository customerRepository;

    public void process(Exchange exchange) {
        userTokenSMS(exchange);
    }

    private void userTokenSMS(Exchange req) {
        OrderRequest body = req.getIn().getBody(OrderRequest.class);

        body.setUserId(Optional.empty());

        Customer customer = customerRepository.findOneByPhoneNumber(body.getPhoneNumber());

        if(customer!=null){
            body.setUserId(Optional.of(customer.getId()));
        }
        /*TODO Throw Error or Exception when user doesnt exist*/

        req.getOut().setBody(body);
    }
}
