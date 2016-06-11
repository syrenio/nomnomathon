package wmpm16.group05.nomnomathon.aggregation;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;

import java.util.ArrayList;

public class CapacityAggregationStrategy implements AggregationStrategy{
	private final static Logger log = Logger.getLogger(CapacityAggregationStrategy.class);
	
    @SuppressWarnings("unchecked")
	@Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        log.debug("Received answers: " + oldExchange + " / " + newExchange + " from " + newExchange.getProperty(Exchange.RECIPIENT_LIST_ENDPOINT, String.class));

        RestaurantCapacityResponse newBody = newExchange.getIn().getBody(RestaurantCapacityResponse.class);
        ArrayList<RestaurantCapacityResponse> list = null;
        if (oldExchange == null) {
            list = new ArrayList<RestaurantCapacityResponse>();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        } else {
            list = oldExchange.getIn().getBody(ArrayList.class);
            list.add(newBody);
            return oldExchange;
        }
    }
}
