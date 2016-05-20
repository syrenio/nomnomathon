package wmpm16.group05.nomnomathon.aggregation;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.models.Customer;

public class EnrichCustomer implements AggregationStrategy{
	private final Logger log = Logger.getLogger(EnrichCustomer.class);
	
	@Override
	public Exchange aggregate(Exchange arg0, Exchange arg1) {
		log.debug("Aggregating " + arg0.getIn().getBody().toString() + " and " + arg1.getIn().getBody().toString());
		OrderRequest orderrequest = (OrderRequest) arg0.getIn().getBody();
		Customer customer = (Customer) arg1.getIn().getBody();
		
		orderrequest.setAdress(customer.getAddress());
		orderrequest.setReceiverName(customer.getFirstName() + " " + customer.getLastName());
		
		return arg0;
	}

}
