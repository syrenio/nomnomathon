package wmpm16.group05.nomnomathon.routers;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.chunk.ChunkConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.models.CustomerNotificationType;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.OrderState;

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
    	
    	/*direct notification to preferred communication channel*/
        from("direct:sendCustomerNotification")
        
        
        //TODO extract orderId & co to header to acces it in template
//        Alt: StringTemplate, FreeMarker
        	.setHeader("orderId").simple("headers.order.orderId")
    		.setHeader("dishes").simple("headers.order.dishes")
	    	//.setHeader("menu").simple("body.menu") // exception: cant find menu in body??
        
	        .choice()
	        	.when(header(NomNomConstants.HEADER_NOTIFICATION_TYPE).isEqualTo(CustomerNotificationType.SMS))
	        		.to("direct:notifyCustomerSms")
		        .when(header(NomNomConstants.HEADER_NOTIFICATION_TYPE).isEqualTo(CustomerNotificationType.MAIL))
	        		.to("direct:notifyCustomerMail")
		        .when(header(NomNomConstants.HEADER_NOTIFICATION_TYPE).isEqualTo(CustomerNotificationType.REST))
	        		.to("direct:notifyCustomerRest")
		    	.otherwise()
	    			//TODO error handling
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:undefined_customerNotificationType?level=ERROR")
	        .end();
	        		
	
        /* Send SMS to Customer */
        from("direct:notifyCustomerSms")
        	.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerSms:send");
    
        /* Send Mail to Customer */
        from("direct:notifyCustomerMail")
	        .setHeader("Content-type", constant("text/html"))
	        .choice()
	        	// set mail subject and Chunk template
        		.when(header("orderState").isEqualTo(OrderState.REJECTED_NO_RESTAURANTS))
		    		.setHeader("subject", constant("NomNom - No Restaurants"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#no_restaurants"))
					
				.when(header("orderState").isEqualTo(OrderState.REJECTED_NO_CAPACITY))
	        		.setHeader("subject", constant("NomNom - No Capacity"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#no_capacity"))
	    			
	        	.when(header("orderState").isEqualTo(OrderState.REJECTED_INVALID_PAYMENT))
        			.setHeader("subject", constant("NomNom - Payment failed"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#invalid_payment"))
	    			
		        .when(header("orderState").isEqualTo(OrderState.FULLFILLED))
        			.setHeader("subject", constant("NomNom - Order finished"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#fullfilled"))
	    			
	    		.otherwise()
	    			//TODO error handling
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:undefined_orderState?level=ERROR")
					.stop()
	        .end()
	        // execute Chunk template and send mail
	        .to("chunk:dummy")
			.to("smtp://" + host + "?password=" + pass + "&username=" + user + "&from=" + user)
			.wireTap("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:send");
    
        /* Send REST to Customer */
        from("direct:notifyCustomerRest")
			.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerRest:send");
        
        
    }
}

