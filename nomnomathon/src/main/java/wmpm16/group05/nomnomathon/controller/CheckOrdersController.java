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
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderRepository;


@RestController
public class CheckOrdersController {
	private Logger log = Logger.getLogger(CheckOrdersController.class);
	
	@Autowired
	private CamelContext context;
	
	@Autowired
    OrderRepository orderRepository;

	@RequestMapping("/demo/checkorders")
	public List<OrderInProcess> startProcess() {
		
		Iterable<OrderInProcess> orders = orderRepository.findAll();
		ArrayList<OrderInProcess> list = new ArrayList<>();
		orders.forEach(o -> list.add(o));
		
		return list; 
		
	}

}
