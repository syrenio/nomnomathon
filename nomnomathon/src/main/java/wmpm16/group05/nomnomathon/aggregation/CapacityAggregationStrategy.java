package wmpm16.group05.nomnomathon.aggregation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;

import java.io.IOException;
import java.util.ArrayList;

public class CapacityAggregationStrategy implements AggregationStrategy{
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        //log.debug("Received answers: " + oldExchange + " / " + newExchange + " from " + newExchange.getProperty(Exchange.RECIPIENT_LIST_ENDPOINT, String.class));
        //log.debug(value);

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
