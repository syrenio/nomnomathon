package wmpm16.group05.nomnomathon.aggregation;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.domain.RestaurantData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agnes on 31.05.16.
 */
public class RestaurantDataAggregation implements AggregationStrategy{

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            List<RestaurantData> restaurantDatas = new ArrayList<>();
            RestaurantData newRestaurantData = newExchange.getIn().getBody(RestaurantData.class);
            restaurantDatas.add(newRestaurantData);
            newExchange.getIn().setBody(restaurantDatas);
            return newExchange;
        }
        List<RestaurantData> restaurants = oldExchange.getIn().getBody(ArrayList.class);
        RestaurantData newRestaurantData = newExchange.getIn().getBody(RestaurantData.class);
        restaurants.add(newRestaurantData);
        oldExchange.getIn().setBody(restaurants);
        return oldExchange;
    }
}
