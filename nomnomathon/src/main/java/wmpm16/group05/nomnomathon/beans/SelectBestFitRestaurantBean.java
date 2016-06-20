package wmpm16.group05.nomnomathon.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;


/**
 * Created by Agnes on 09.06.16.
 */
@Component
public class SelectBestFitRestaurantBean {

	/**
	 * Looks for the cheapest restaurant.
	 * @param exchange
	 */

    @SuppressWarnings("unchecked")
	public void process(Exchange exchange) {
        List<RestaurantData> restaurants = exchange.getIn().getBody(ArrayList.class);
        List<String> disheNames = exchange.getIn().getHeader(NomNomConstants.HEADER_DISHES_ORDER, ArrayList.class);

        //actual best price
        double bestPrice = 0;
        //first solution is assumed best, is set to false for restaurants after
        boolean firstrun = true;
        //actual best restaurant
        int selectedRestaurantId = 0;
        //actual best combination of dishes
        Map<String, Double> dishesPrices = new HashMap<>();
        
        //iterable looks for best restaurant
        for (RestaurantData r : restaurants) {
        	//total for actual restaurant
            double sumPrices = 0;
            //dishes and prices for actual restaurant
            Map<String, Double> dishesPricesTemporary = new HashMap<>();
            for (Menu m : r.getMenu()) {
                if (disheNames.contains(m.getName())) {
                    sumPrices += m.getPrice();
                    dishesPricesTemporary.put(m.getName(), m.getPrice());
                }
            }
            if(firstrun || sumPrices < bestPrice) {
            	firstrun = false;
            	bestPrice = sumPrices;
            	selectedRestaurantId = r.get_id();
            	dishesPrices = dishesPricesTemporary;            	
            }            
        }
        

        exchange.getIn().setHeader(NomNomConstants.HEADER_RESTAURANT_ID, selectedRestaurantId);
        exchange.getIn().setHeader(NomNomConstants.HEADER_AMOUNT, bestPrice);
        exchange.getIn().setHeader(NomNomConstants.HEADER_DISHES_PRICES, dishesPrices);
    }
}
