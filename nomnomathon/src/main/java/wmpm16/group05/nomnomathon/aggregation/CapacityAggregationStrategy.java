package wmpm16.group05.nomnomathon.aggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class CapacityAggregationStrategy implements AggregationStrategy{
	   public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
	        if (oldExchange == null) {
	            List<Long> ids = new ArrayList<>();
	            Long newId= Long.parseLong(newExchange.getIn().getBody(String.class));
	           ids.add(newId);
	            newExchange.getIn().setBody(ids);
	            return newExchange;
	        }
	        List<Long> ids = oldExchange.getIn().getBody(ArrayList.class);
            Long newId= Long.parseLong(newExchange.getIn().getBody(String.class));
	        ids.add(newId);
	        oldExchange.getIn().setBody(ids);
	        return oldExchange;
	    }

}