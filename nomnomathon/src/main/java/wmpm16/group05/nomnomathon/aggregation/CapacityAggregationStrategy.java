package wmpm16.group05.nomnomathon.aggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class CapacityAggregationStrategy implements AggregationStrategy{
	   public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            
		   List<Long> ids = new ArrayList<>();
		   Long newId = Long.parseLong(newExchange.getIn().getBody(String.class));

		   if (newId > -1L) {
			   if (oldExchange != null) {
				   ids = oldExchange.getIn().getBody(ArrayList.class);
			   }
			   
			   ids.add(newId);
			   newExchange.getIn().setBody(ids);
		   } else {
			   newExchange = oldExchange;			   
		   }
		   return newExchange;
	    }

}