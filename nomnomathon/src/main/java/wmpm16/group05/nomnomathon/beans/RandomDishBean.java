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

    private Menu selectedDish;
    private RestaurantData selectedRestaurant;

    public void process(Exchange exchange) {
        ArrayList<RestaurantData> restaurants = exchange.getIn().getBody(ArrayList.class);
        selectRandomRestaurant(restaurants);
        exchange.getIn().setHeader("restaurantId", selectedRestaurant.get_id());
        exchange.getIn().setHeader("restaurantName", selectedRestaurant.getName());
        exchange.getIn().setHeader("dishName", selectedDish.getName());
        exchange.getIn().setHeader("dishPrice", selectedDish.getPrice());
        System.out.println("restaurant: "+exchange.getIn().getHeader("restaurantName")+", "+exchange.getIn().getHeader("dishName"));
    }

    private void selectRandomRestaurant(ArrayList<RestaurantData> restaurants) {
        selectedRestaurant = restaurants.get(calculateRandomValue(0, restaurants.size()-1));
        selectRandomDish();
    }

    private void selectRandomDish() {
        ArrayList<Menu> dishes = selectedRestaurant.getMenu();
        selectedDish = dishes.get(calculateRandomValue(0, dishes.size()-1));
    }

    private int calculateRandomValue(int min, int max) {
        int r = min + (int)(Math.random() * ((max - min) + 1));
        return r;
    }
}
