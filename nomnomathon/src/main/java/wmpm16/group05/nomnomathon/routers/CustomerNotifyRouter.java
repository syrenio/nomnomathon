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

		
    @Override
    public void configure() throws Exception {
    	
    	
    	//TODO Templates anpassen
    	
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
			    .when(simple("${header." + NomNomConstants.HEADER_NOTIFICATION_TYPE + "} =~ '" + CustomerNotificationType.SMS + "'"
			    		+ " && ${body?.customer?.phoneNumber} regex '{{smpp.regex}}'"))
		    		.setHeader(NomNomConstants.HEADER_SMPP_PHONENUMBER).simple("body.customer.phoneNumber")
	        		.to("direct:notifyCustomerSms")
			    .when(simple("${header." + NomNomConstants.HEADER_NOTIFICATION_TYPE + "} =~ '" + CustomerNotificationType.MAIL + "'"
			    		+ " && ${body?.customer?.mail} regex '{{mail.regex}}'"))
			        .setHeader(NomNomConstants.HEADER_SMTP_TO).simple("body.customer.mail")
	        		.to("direct:notifyCustomerMail")
		        .when(header(NomNomConstants.HEADER_NOTIFICATION_TYPE).isEqualTo(CustomerNotificationType.REST))
	        		.to("direct:notifyCustomerRest")
		    	.otherwise()
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:undefined_customerNotification?level=ERROR")
					.stop()
	        .end();
   
        /* Send SMS to Customer */
        from("direct:notifyCustomerSms")
			.wireTap("metrics:counter:notify:SMS?increment=1")
	        .choice()
	    		// set Chunk template
				.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_RESTAURANTS))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.sms.no_restaurants}}"))
					
				.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_CAPACITY))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.sms.no_capacity}}"))
					
		    	.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_INVALID_PAYMENT))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.sms.invalid_payment}}"))
					
		        .when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.FULLFILLED))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.sms.fullfilled}}"))
					
				.otherwise()
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerSms:undefined_orderState?level=ERROR")
					.stop()
		    .end()
		    // execute Chunk template and send SMS
		    .to("chunk:dummy")
	        .to("smpp://{{smpp.user}}@{{smpp.host}}:{{smpp.port}}" 
	        		+ "?password={{smpp.pass}}" 
	        		+ "&enquireLinkTimer=6000&systemType=producer&registeredDelivery=0")
        	.wireTap("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerSms:send?level=DEBUG&showBody=false")
        	.end();
    
        /* Send Mail to Customer */
        from("direct:notifyCustomerMail")
        	.wireTap("metrics:counter:notify:MAIL?increment=1")
	        .setHeader("Content-type", constant("text/html"))
	        .choice()
	        	// set mail subject and Chunk template
        		.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_RESTAURANTS))
		    		.setHeader(NomNomConstants.HEADER_SUBJECT, constant("{{tpl.mail.subject.no_restaurants}}"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.mail.no_restaurants}}"))
					
				.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_CAPACITY))
	        		.setHeader(NomNomConstants.HEADER_SUBJECT, constant("{{tpl.mail.subject.no_capacity}}"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.mail.no_capacity}}"))
	    			
	        	.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_INVALID_PAYMENT))
        			.setHeader(NomNomConstants.HEADER_SUBJECT, constant("{{tpl.mail.subject.invalid_payment}}"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.mail.invalid_payment}}"))
	    			
		        .when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.FULLFILLED))
		        	.setHeader(NomNomConstants.HEADER_SUBJECT, constant("{{tpl.mail.subject.fullfilled}}"))
		    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.mail.fullfilled}}"))
	    			
	    		.otherwise()
					.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:undefined_orderState?level=ERROR")
					.stop()
	        .end()
	        // execute Chunk template and send mail
	        .to("chunk:dummy")
			.to("smtp://{{mail.host}}:{{mail.port}}?"
					+ "password={{mail.pass}}&"
					+ "username={{mail.user}}&"
					+ "from={{mail.user}}")
			.wireTap("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerMail:send?level=DEBUG&showBody=false")
			.end();
    
        /* Send REST to Customer */
        from("direct:notifyCustomerRest")
        //TODO
        .choice()
    		// set Chunk template
			.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_RESTAURANTS))
	    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.rest.no_restaurants}}"))
				
			.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_CAPACITY))
	    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.rest.no_capacity}}"))
				
	    	.when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_INVALID_PAYMENT))
	    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.rest.invalid_payment}}"))
				
	        .when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.FULLFILLED))
	    		.setHeader(ChunkConstants.CHUNK_RESOURCE_URI, constant("{{tpl.rest.fullfilled}}"))
				
			.otherwise()
				.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerRest:undefined_orderState?level=ERROR")
				.stop()
	    .end()
		.to("log:wmpm16.group05.nomnomathon.routers.CustomerNotifyRouter.notifyCustomerRest:send?level=DEBUG&showBody=false");
        
        
    }
}

