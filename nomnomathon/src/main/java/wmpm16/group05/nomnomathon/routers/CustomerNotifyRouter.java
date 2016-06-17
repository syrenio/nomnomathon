package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.chunk.ChunkConstants;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.beans.LoadOrderBean;
import wmpm16.group05.nomnomathon.models.CustomerNotificationType;
import wmpm16.group05.nomnomathon.models.OrderState;

/**
 * Route to notify users on status changes of his order.
 */
@Component
public class CustomerNotifyRouter extends RouteBuilder {

//	@Value("${mail.host}")
//	private String host;
//	
//	@Value("${mail.port}")
//	private String port;
//	
//	@Value("${mail.pass}")
//	private String pass;
//	
//	@Value("${mail.user}")
//	private String user;
	
	
    @Override
    public void configure() throws Exception {
    	
    	//configure redelivery of failed notifications
    	errorHandler(deadLetterChannel("seda:errors")
    					.maximumRedeliveries(20)
    					.delayPattern("1:3000;5:5000")
    					.retryAttemptedLogLevel(LoggingLevel.DEBUG)
    					.log("notifyCustomer:deliveryFailed")
    					.logHandled(true));
    	
    	/*direct notification to preferred communication channel*/
        from("direct:sendCustomerNotification")
        
        	.bean(LoadOrderBean.class)

	    	.setHeader(NomNomConstants.HEADER_ORDER_ID).simple("body.orderId")
	    	.setHeader(NomNomConstants.HEADER_NOTIFICATION_TYPE).simple("body.customer.notificationType")
	    	.setHeader(NomNomConstants.HEADER_FIRST_NAME).simple("body.customer.firstName")
	    	.setHeader(NomNomConstants.HEADER_LAST_NAME).simple("body.customer.lastName")
	    	.setHeader(NomNomConstants.HEADER_DISHES_ORDER).simple("body.dishes")

        
	        .choice()
	        	.when(header(NomNomConstants.HEADER_NOTIFICATION_TYPE).isEqualTo(CustomerNotificationType.SMS))
		    		.setHeader(NomNomConstants.HEADER_SMS_PHONENUMBER).simple("body.customer.phoneNumber")
	        		.to("direct:notifyCustomerSms")
		        .when(header(NomNomConstants.HEADER_NOTIFICATION_TYPE).isEqualTo(CustomerNotificationType.MAIL))
		    		.setHeader(NomNomConstants.HEADER_MAIL_TO).simple("body.customer.mail")
	        		.to("direct:notifyCustomerMail")
		        .when(header(NomNomConstants.HEADER_NOTIFICATION_TYPE).isEqualTo(CustomerNotificationType.REST))
	        		.to("direct:notifyCustomerRest")
		    	.otherwise()
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:undefined_customerNotificationType?level=ERROR")
					.stop()
	        .end();
	        		
	
        /* Send SMS to Customer */
        from("direct:notifyCustomerSms")
	        .choice()
	    		// set Chunk template
				.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_RESTAURANTS))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("sms#no_restaurants"))
					
				.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_CAPACITY))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("sms#no_capacity"))
					
		    	.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_INVALID_PAYMENT))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("sms#invalid_payment"))
					
		        .when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.FULLFILLED))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("sms#fullfilled"))
					
				.otherwise()
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerSms:undefined_orderState?level=ERROR")
					.stop()
		    .end()
		    // execute Chunk template and send SMS
		    .to("chunk:dummy")
		    .process(x -> {System.err.println("CustomerNotifyRouter.java: START http://www.seleniumsoftware.com/downloads.html FOR SMS. Afterwards uncomment the code block: .to(\"smpp");})
	        /*.to("smpp://"
	        		+ NomNomConstants.SMPP_USER + "@"
	        		+ NomNomConstants.SMPP_HOST + ":"
	        		+ NomNomConstants.SMPP_PORT + "?password="
	        		+ NomNomConstants.SMPP_PASS 
	        		+ "&enquireLinkTimer=6000&systemType=producer&registeredDelivery=0")
        	.wireTap("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerSms:send")*/
        	.end();
    
        /* Send Mail to Customer */
        from("direct:notifyCustomerMail")
	        .setHeader("Content-type", constant("text/html"))
	        .choice()
	        	// set mail subject and Chunk template
        		.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_RESTAURANTS))
		    		.setHeader("subject", constant("NomNom - No Restaurants"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#no_restaurants"))
					
				.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_CAPACITY))
	        		.setHeader("subject", constant("NomNom - No Capacity"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#no_capacity"))
	    			
	        	.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_INVALID_PAYMENT))
        			.setHeader("subject", constant("NomNom - Payment failed"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#invalid_payment"))
	    			
		        .when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.FULLFILLED))
        			.setHeader("subject", constant("NomNom - Order finished"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("mail#fullfilled"))
	    			
	    		.otherwise()
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:undefined_orderState?level=ERROR")
					.stop()
	        .end()
	        // execute Chunk template and send mail
	        .to("chunk:dummy")
			.to("smtp://" 
					+ NomNomConstants.MAIL_HOST + "?password=" 
					+ NomNomConstants.MAIL_PASS + "&username=" 
					+ NomNomConstants.MAIL_USER + "&from=" 
					+ NomNomConstants.MAIL_USER)
			.wireTap("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:send")
			.end();
    
        /* Send REST to Customer */
        from("direct:notifyCustomerRest")
        //TODO
			.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerRest:send");
        
        
    }
}

