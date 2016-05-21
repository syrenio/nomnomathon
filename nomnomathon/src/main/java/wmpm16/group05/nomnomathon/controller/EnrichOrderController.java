package wmpm16.group05.nomnomathon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;


@RestController
public class EnrichOrderController {
	private Logger log = Logger.getLogger(EnrichOrderController.class);
	
	@Autowired
	private CamelContext context;
	
	@Autowired
    CustomerRepository customerRepository;

	@RequestMapping("/demo/EnrichOrder")
	public OrderRequest startProcess() {
		
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

}
