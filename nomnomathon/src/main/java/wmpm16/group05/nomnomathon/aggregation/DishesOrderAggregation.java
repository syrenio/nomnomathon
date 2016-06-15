package wmpm16.group05.nomnomathon.aggregation;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agnes on 24.05.16.
 */
public class DishesOrderAggregation implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        System.out.println(("Aggregating " + oldExchange.getIn().getBody().toString() + " and " + newExchange.getIn().getBody().toString()));
        OrderInProcess order = newExchange.getIn().getBody(OrderInProcess.class);
        List<String> dishesInOrderNames = new ArrayList<>();
        for(Dish d: order.getDishes()){
            dishesInOrderNames.add(d.getDish());
        }

        oldExchange.getIn().setHeader(NomNomConstants.HEADER_DISHES_ORDER, dishesInOrderNames);

        return oldExchange;
    }
}
