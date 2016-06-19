package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by syrenio on 5/20/2016.
 */
@Component
public class RegularAuthBean {
    private final Logger log = Logger.getLogger(RegularAuthBean.class);

    private static final String HEADER_Authorization = "Authorization";

    @Autowired
    private CustomerRepository customerRepository;

    public void process(Exchange exchange){
        userTokenREGULAR(exchange);
    }

    private void userTokenREGULAR(Exchange req){
        OrderRequest body = req.getIn().getBody(OrderRequest.class);
        String auth_header = (String) req.getIn().getHeader(HEADER_Authorization);
        String username = null;
        String password = null;

        try{
            if (auth_header != null && auth_header.startsWith("Basic")) {
                final String[] values = extractBasicAuth(auth_header);
                if(values.length == 2) {
                    username = values[0];
                    password = values[1];
                }
            }
        }catch (Exception ex){
            log.error("Incorrect auth header: " + auth_header);
        }

        body.setUserId(Optional.empty());

        if(username!= null && password != null){
            Optional<Customer> cust = customerRepository.findOneByUserNameAndPassword(username,password);
            if(cust.isPresent()){
                body.setUserId(Optional.of(cust.get().getId()));
            }
        }

        req.getOut().setBody(body);
    }

    private String[] extractBasicAuth(String auth_header) {
        // Authorization: Basic base64credentials
        String base64Credentials = auth_header.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                Charset.forName("UTF-8"));
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        return values;
    }
}
