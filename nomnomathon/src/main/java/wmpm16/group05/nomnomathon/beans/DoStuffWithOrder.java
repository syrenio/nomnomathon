package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderRepository;
import wmpm16.group05.nomnomathon.models.OrderState;

@Component
public class DoStuffWithOrder {

	private final Logger log = Logger.getLogger(DoStuffWithOrder.class);

	@Autowired
	OrderRepository orderRepository;

	public void doStuff(Exchange ex) {
		OrderInProcess order = ex.getIn().getBody(OrderInProcess.class);
		order.setState(OrderState.FULLFILLED);

		if (orderRepository != null) {
			orderRepository.save(order);
		} else {
			log.error("Could not save, orderRepository is null");

		}

	}
}
