package wmpm16.group05.nomnomathon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import wmpm16.group05.nomnomathon.domain.Dish;
import wmpm16.group05.nomnomathon.domain.Order;
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
	public Order startProcess() {
		
		Optional<Customer> opcustomer = customerRepository.findOneByUserNameAndPassword("bernd","nomnom");
		log.debug("Found customer: " + (opcustomer.isPresent() ? opcustomer.get() : "No customer found - exiting"));
		
		if(!opcustomer.isPresent()) {
			log.error("No customer available");
			return null;
			
		
		}
		Customer customer = opcustomer.get();
		Optional<Long> oprestaurantid = Optional.ofNullable(null);
		List<Dish> disheslist = new ArrayList<>();
		
		ProducerTemplate template = context.createProducerTemplate();
		Order order = (Order) template.requestBody("direct:enrichCustomerData", new Order(customer.getId(), oprestaurantid, disheslist));
		
		return order;
		
	}

}
