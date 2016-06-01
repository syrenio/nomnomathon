package wmpm16.group05.nomnomathon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.mocked.OrderRequestAnswer;
import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderState;




@RestController
public class TestController {
	private Logger log = Logger.getLogger(TestController.class);
	
	@Autowired
	private CamelContext context;
	
	@Autowired
    CustomerRepository customerRepository;

	@RequestMapping("/test/EnrichOrder")
	public OrderRequest testEnrichCustomer() {
		
		Optional<Customer> opcustomer = customerRepository.findOneByUserNameAndPassword("bernd","nomnom");
		log.debug("Found customer: " + (opcustomer.isPresent() ? opcustomer.get() : "No customer found - exiting"));
		
		if(!opcustomer.isPresent()) {
			log.error("No customer available");
			return null;
			
		
		}
		Customer customer = opcustomer.get();
		Optional<Long> oprestaurantid = Optional.ofNullable(null);
		List<String> disheslist = new ArrayList<>();
		
		OrderRequest orderReq = new OrderRequest();
		orderReq.setUserId(Optional.of(customer.getId()));
		orderReq.setRestaurantId(oprestaurantid);
		orderReq.setDishes(disheslist);
		
		
		ProducerTemplate template = context.createProducerTemplate();
		OrderRequest response = (OrderRequest) template.requestBody("direct:enrichCustomerData", orderReq);
		
		return response;
		
	}

	@RequestMapping("/test/testRestaurantStatus")
	public void testRestaurantStatus() {
		log.debug("START TestRestaurantStatus");
		
		try {
			context.addRoutes(new RouteBuilder() {

				@Override
				public void configure() throws Exception {
					from("direct:testRestaurantStatus")
							.marshal().json(JsonLibrary.Jackson)
							.setHeader("Exchange.HTTP_METHOD", constant("GET"))
							.to("http://localhost:8080/external/restaurants/pizzza/capacity");
					
				}});
			log.debug("ROUTE added");
		} catch (Exception e) {
			
			log.error(e);
		}
		OrderInProcess order = new OrderInProcess();
		
		ProducerTemplate template = context.createProducerTemplate();
		template.requestBody("direct:testRestaurantStatus", order);
		
		
		log.debug("END TestRestaurantStatus");
		
	}
	
	@RequestMapping("/test/testRestaurantOrder")
	public OrderRequestAnswer testRestaurantOrder() {
		log.debug("START TestRestaurantOrder");
		
		try {
			context.addRoutes(new RouteBuilder() {

				@Override
				public void configure() throws Exception {
					from("direct:testRestaurantOrder")
							.to("log:wmpm16.group05.nomnomathon.controller.TestController.TestOrder?level=DEBUG")
							.marshal().json(JsonLibrary.Jackson)
							.setHeader(Exchange.HTTP_METHOD, constant("POST"))
							.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
							.to("log:wmpm16.group05.nomnomathon.controller.TestController.TestOrder?level=DEBUG")
							.to("http://localhost:8080/external/restaurants/pizzza/order")
							.unmarshal().json(JsonLibrary.Jackson, OrderRequestAnswer.class)
							.to("log:wmpm16.group05.nomnomathon.controller.TestController.TestOrder?level=DEBUG");
					
				}});
			log.debug("ROUTE added");
		} catch (Exception e) {
			
			log.error(e);
		}
		OrderInProcess order = new OrderInProcess();
		order.addDish("Sushi");
		order.addDish("Pizza");
		order.setState(OrderState.CREATED);
		order.setOrderId(123L);
		
		log.debug(order);
		
		ProducerTemplate template = context.createProducerTemplate();
		OrderRequestAnswer answer = (OrderRequestAnswer) template.requestBody("direct:testRestaurantOrder", order);
		
		
		log.debug("END TestRestaurantOrder");
		
		return answer;
		
	}
	

	
}