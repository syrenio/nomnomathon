package wmpm16.group05.nomnomathon.routers;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Route to notify users on status changes of his order.
 */
@Component
public class CustomerNotifyRouter extends RouteBuilder {

	@Value("${mail.host}")
	private String host;
	
	@Value("${mail.port}")
	private String port;
	
	@Value("${mail.pass}")
	private String pass;
	
	@Value("${mail.user}")
	private String user;
	
	
    @Override
    public void configure() throws Exception {
    	

    	
    	
        from("direct:mail")
        .setHeader("subject", constant("Hello"))
        .setBody(body().append(" World!"))
        .wireTap("log:mail:send")
                .to("smtp://" + host + "?password=" + pass + "&username=" + user + "&from=" + user + "&to=notify@nomnomathon.eu");

    }
}