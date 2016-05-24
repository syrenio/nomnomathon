package wmpm16.group05.nomnomathon.beans;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderState;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Agnes on 21.05.16.
 */
@Component
public class QueryRestaurantBean {

    public void process(Exchange exchange) {
        System.out.println("query restaurant");
        OrderInProcess order = exchange.getIn().getHeader("order", OrderInProcess.class);
        List<Dish> dishesInOrder = order.getDishes();

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

        List<String> dishesName = new ArrayList<>();

        for (BasicDBObject l : dishes) {
            dishesName.add((String) l.get("name"));
        }

        for (Dish d : dishesInOrder) {
            if(dishesName.contains(d.getDish())){
                System.out.println(d.getDish()+" can be delivered");
            }else{
                System.out.println(d.getDish()+" cannot be delivered: no suitable restaurant found --> Order rejected");
                exchange.getIn().setHeader("orderState", OrderState.REJECTED_NO_RESTAURANTS);
            }
        }

    }


}
