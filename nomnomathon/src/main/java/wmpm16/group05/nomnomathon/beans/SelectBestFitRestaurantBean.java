package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

import java.math.BigDecimal;
import java.util.*;


/**
 * Created by Agnes on 09.06.16.
 */
@Component
public class SelectBestFitRestaurantBean {


    public void process(Exchange exchange) {
        List<RestaurantData> restaurants = exchange.getIn().getBody(ArrayList.class);
        List<String> disheNames = exchange.getIn().getHeader(RESTRouter.HEADER_DISHES_ORDER, ArrayList.class);

        Map<Integer, Integer> restauransTotalPrices = new HashMap<>();
        PriorityQueue<Integer> pricesQueue = new PriorityQueue<>();
        Map<String, Double> dishesPrices = new HashMap<>();
        BigDecimal sum = new BigDecimal(0d);
        for (RestaurantData r : restaurants) {
            int sumPrices = 0;
            for (Menu m : r.getMenu()) {
                if (disheNames.contains(m.getName())) {
                    sumPrices += m.getPrice();
                    dishesPrices.put(m.getName(), m.getPrice());
                    sum = sum.add(BigDecimal.valueOf(m.getPrice()));
                }
            }
            restauransTotalPrices.put(sumPrices, r.get_id());
            pricesQueue.add(sumPrices);
        }
        int bestPrice = pricesQueue.poll();
        int selectedRestaurantId = restauransTotalPrices.get(bestPrice);

        exchange.getIn().setHeader(RESTRouter.HEADER_ORDER_TOTAL_PRICE_OF_DISHES, dishesPrices);
        exchange.getIn().setHeader(RESTRouter.HEADER_RESTAURANT_ID, selectedRestaurantId);
        exchange.getIn().setHeader(RESTRouter.HEADER_AMOUNT, sum.doubleValue());
        exchange.getIn().setHeader(RESTRouter.HEADER_DISHES_PRICES, dishesPrices);
    }
}
