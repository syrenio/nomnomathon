package wmpm16.group05.nomnomathon.beans;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
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

    BasicDBObject randomDishObject;

    public void process(Exchange exchange) {
        System.out.println("type: " + exchange.getIn().getHeader("type"));

        OrderInProcess order = exchange.getIn().getHeader("order", OrderInProcess.class);
        List<BasicDBObject> restaurants = exchange.getIn().getBody(ArrayList.class);
        System.out.println("results: " + restaurants.size() + " restaurant/s found");

        List<BasicDBObject> dishes = new ArrayList<>();
        BasicDBList menu = new BasicDBList();
        for (BasicDBObject obj : restaurants) {
            menu = (BasicDBList) obj.get("menu");
        }
        for (Object m : menu) {
            dishes.add((BasicDBObject) m);
        }
        selectRandomDish(dishes);
        Dish randomDish = new Dish();
        randomDish.setDish((String) randomDishObject.get("name"));
        randomDish.setPrice((double) randomDishObject.get("price"));
        List<Dish> rDishes = new ArrayList<>();
        rDishes.add(randomDish);
        order.setDishes(rDishes);

        exchange.getIn().setHeader("orderState", OrderState.FULLFILLED);

        System.out.println("order: " + exchange.getIn().getHeader("order"));
    }


    private void selectRandomDish(List<BasicDBObject> dishes) {
        int r = (int) (Math.random() * (dishes.size() - 1)) + 1;
        randomDishObject = dishes.get(r);

        System.out.println("selected random dish: " + randomDishObject.get("name"));

    }
}
