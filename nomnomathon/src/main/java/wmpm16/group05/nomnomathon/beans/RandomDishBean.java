package wmpm16.group05.nomnomathon.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.models.OrderState;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;

/**
 * Created by Agnes on 22.05.16.
 */
@Component
public class RandomDishBean {

    private Menu selectedDish;
    private List<RestaurantData> selectedRestaurant;

    public void process(Exchange exchange) {
        ArrayList<RestaurantData> restaurants = exchange.getIn().getBody(ArrayList.class);
        selectedRestaurant = new ArrayList<>();
        selectRandomRestaurant(restaurants);
        Map<String, Double> dishesPrices = new HashMap<>();
        dishesPrices.put(selectedDish.getName(), selectedDish.getPrice());

        List<String> restaurantIds = new ArrayList<>();
        restaurantIds.add(String.valueOf(selectedRestaurant.get(0).get_id()));

        exchange.getIn().setHeader(NomNomConstants.HEADER_RESTAURANT_ID, selectedRestaurant.get(0).get_id());
        /*contains all restaurant ids for capacity check*/
        exchange.getIn().setHeader(NomNomConstants.HEADER_RESTAURANTS, restaurantIds);
        exchange.getIn().setHeader(NomNomConstants.HEADER_DISHES_PRICES, dishesPrices);
        exchange.getIn().setHeader(NomNomConstants.HEADER_ORDER_STATE, OrderState.ENRICHED);
        exchange.getIn().setHeader(NomNomConstants.HEADER_DISHES_ORDER, selectedDish.getName());

    }

    private void selectRandomRestaurant(ArrayList<RestaurantData> restaurants) {
        selectedRestaurant.add(restaurants.get(calculateRandomValue(0, restaurants.size() - 1)));
        selectRandomDish();
    }

    private void selectRandomDish() {
        ArrayList<Menu> dishes = selectedRestaurant.get(0).getMenu();
        selectedDish = dishes.get(calculateRandomValue(0, dishes.size() - 1));
    }

    private int calculateRandomValue(int min, int max) {
        int r = min + (int) (Math.random() * ((max - min) + 1));
        return r;
    }
}
