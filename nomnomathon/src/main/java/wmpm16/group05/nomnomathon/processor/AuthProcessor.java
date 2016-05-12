package wmpm16.group05.nomnomathon.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by syrenio on 5/10/2016.
 */
@Component
public class AuthProcessor implements Processor{

    private static final String HEADER_Authorization = "Authorization";

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void process(Exchange req) throws Exception {
        /*extract userId from header: Authorization  --> Basic Base64(username:password)*/
        /*maybe use X-Auth-Token or Authorization*/

        OrderRequest body = req.getIn().getBody(OrderRequest.class);

        if(body.getType() == OrderType.REGULAR){
            userTokenREGULAR(req);
        }else if(body.getType() == OrderType.SMS){
            userTokenSMS(req);
        }
    }

    private void userTokenSMS(Exchange req) {
        OrderRequest body = req.getIn().getBody(OrderRequest.class);

        Customer customer = customerRepository.findOneByPhoneNumber(body.getPhoneNumber());

        if(customer!=null){
            body.setUserId(Optional.of(customer.getId()));
        }
        /*TODO Throw Error or Exception when user doesnt exist*/

        req.getIn().setBody(body);
    }

    private void userTokenREGULAR(Exchange req){
        OrderRequest body = req.getIn().getBody(OrderRequest.class);
        String auth_header = (String) req.getIn().getHeader(HEADER_Authorization);
        String username = null;
        String password = null;

        if (auth_header != null && auth_header.startsWith("Basic")) {
            final String[] values = extractBasicAuth(auth_header);
            username = values[0];
            password = values[1];
        }

        if(username!= null && password != null){
            Optional<Customer> cust = customerRepository.findOneByUserNameAndPassword(username,password);
            if(cust.isPresent()){
                body.setUserId(Optional.of(cust.get().getId()));
            }
        }
        /*TODO Throw Error or Exception when user doesnt exist*/


        req.getIn().setBody(body);
        System.out.println(body.getUserId().get());
    }

    private String[] extractBasicAuth(String auth_header) {
        // Authorization: Basic base64credentials
        String base64Credentials = auth_header.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                Charset.forName("UTF-8"));
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        System.out.println("auth: " + values[0] + ":"+ values[1]);
        return values;
    }
}
