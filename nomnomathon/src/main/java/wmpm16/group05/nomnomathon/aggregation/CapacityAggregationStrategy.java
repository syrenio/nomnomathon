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
	            /* All ids = -1 indicate the restaurant has no capacity and are therefore not added */
	            if(newId > -1){ids.add(newId);}
	            newExchange.getIn().setBody(ids);
	            return newExchange;
	        }
	        List<Long> ids = oldExchange.getIn().getBody(ArrayList.class);
            Long newId= Long.parseLong(newExchange.getIn().getBody(String.class));
	        if(newId > -1){ids.add(newId);}
	        oldExchange.getIn().setBody(ids);
	        return oldExchange;
	    }

}