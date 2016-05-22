package wmpm16.group05.nomnomathon.beans;

import java.sql.Timestamp;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderListEntry;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.DishRepository;
import wmpm16.group05.nomnomathon.models.OrderListEntryRepository;
import wmpm16.group05.nomnomathon.models.OrderRepository;

@Component
public class StoreOrderBean {

	@Autowired
	OrderListEntryRepository orderListEntryRepository;
	
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
		OrderListEntry orderListEntry;
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		/* Store everything available yet */ 
		//TODO extend to all fields != null
		/*
		for (Dish dish : order.getDishes()) {
			orderListEntry = new OrderListEntry();

			orderListEntry.setCustomerId(order.getCustomer().getId());
			orderListEntry.setDish(dish.getDish());
			orderListEntry.setLastChange(ts);
			//orderListEntry.setOrderId(order.getOrderId());
			orderListEntry.setState(order.getState());
			
			orderListEntryRepository.save(orderListEntry);
		}
		*/
		
		exchange.setOut(exchange.getIn());
		
	}
		
}
