package wmpm16.group05.nomnomathon.beans;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agnes on 22.05.16.
 */
@Component
public class RandomDishBean {

    public void process(Exchange exchange) {
        System.out.println("type: " + exchange.getIn().getHeader("type"));

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
    }

    private void selectRandomDish(List<BasicDBObject> dishes) {
        int random = (int) Math.random() * dishes.size();
        BasicDBObject randomDishName = dishes.get(random);

        System.out.println("selected random dish: " + randomDishName.get("name"));
    }
}
