package wmpm16.group05.nomnomathon.beans;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agnes on 22.05.16.
 */
@Component
public class RandomDishBean {
//sammeln aller Restaurants (extra bean)
    private static Menu selectedDish;
    private static RestaurantData selectedRestaurant;

    public void process(Exchange exchange) {
        ArrayList<RestaurantData> restaurants = exchange.getIn().getBody(ArrayList.class);
        System.out.println("res: "+restaurants);
        selectedRestaurant = restaurants.get(0);
        selectedDish = selectedRestaurant.getMenu().get(0);
     //   selectRandomRestaurant(restaurants);
        exchange.getIn().setHeader("restaurantId", selectedRestaurant.get_id());
        exchange.getIn().setHeader("dish", selectedDish.getName());
        System.out.println("restaurant: "+exchange.getIn().getHeader("restaurantId")+", "+exchange.getIn().getHeader("dish"));
    }

    private void selectRandomRestaurant(ArrayList<RestaurantData> restaurants) {
        selectedRestaurant = restaurants.get(calculateRandomValue(0, restaurants.size()));
        selectRandomDish();
    }

    private void selectRandomDish() {
        ArrayList<Menu> dishes = selectedRestaurant.getMenu();
        selectedDish = dishes.get(calculateRandomValue(0, dishes.size()));
    }

    private int calculateRandomValue(int min, int max) {
        int r = min + (int)(Math.random() * ((max - min) + 1));
        return r;
    }
}
