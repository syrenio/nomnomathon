package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.DishRepository;
import wmpm16.group05.nomnomathon.models.OrderRepository;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;

@Component
public class StoreOrderBean {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	DishRepository dishRepository;

	
	public void save(Exchange exchange){

		OrderInProcess order = exchange.getIn().getBody(OrderInProcess.class);
		for(Dish d: order.getDishes()) {
			dishRepository.save(d);
		}
		orderRepository.save(order);
		
		exchange.setOut(exchange.getIn());
		exchange.getOut().setHeader(NomNomConstants.HEADER_ORDER_ID, order.getOrderId());
		
	}
		
}
