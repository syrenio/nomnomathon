package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

import java.util.*;


/**
 * Created by Agnes on 09.06.16.
 */
@Component
public class SelectBestFitRestaurantBean {


    public void process(Exchange exchange) {
        List<RestaurantData> restaurants = exchange.getIn().getBody(ArrayList.class);
        List<String> disheNames = exchange.getIn().getHeader(RESTRouter.HEADER_DISHES_ORDER, ArrayList.class);

        Map<Double, Integer> restauransTotalPrices = new HashMap<>();
        PriorityQueue<Double> pricesQueue = new PriorityQueue<>();
        Map<String, Double> dishesPrices = new HashMap<>();
        for (RestaurantData r : restaurants) {
            double sumPrices = 0;
            for (Menu m : r.getMenu()) {
                if (disheNames.contains(m.getName())) {
                    sumPrices += m.getPrice();
                    dishesPrices.put(m.getName(), m.getPrice());
                }
            }
            restauransTotalPrices.put(sumPrices, r.get_id());
            pricesQueue.add(sumPrices);
        }
        double bestPrice = pricesQueue.poll();
        int selectedRestaurantId = restauransTotalPrices.get(bestPrice);

        exchange.getIn().setHeader(RESTRouter.HEADER_RESTAURANT_ID, selectedRestaurantId);
        exchange.getIn().setHeader(RESTRouter.HEADER_AMOUNT, bestPrice);
        exchange.getIn().setHeader(RESTRouter.HEADER_DISHES_PRICES, dishesPrices);
    }
}