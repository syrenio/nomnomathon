package wmpm16.group05.nomnomathon.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by syrenio on 5/10/2016.
 */
@Component
public class AuthProcessor implements Processor{

    private static final String HEADER_Authorization = "Authorization";

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

        if(body.getPhoneNumber().equals("+4368012345678")){
            body.setUserId(Optional.of(159l));
        }

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

                    /*TODO check in DB*/
                    /*TODO insert UserID into order-request*/

        if(username!= null && password != null){
            if(username.equals("bernd") && password.equals("nomnom")){
                body.setUserId(Optional.of(159l));
            }
        }
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
