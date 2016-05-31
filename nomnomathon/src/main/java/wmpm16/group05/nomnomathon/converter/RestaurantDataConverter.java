package wmpm16.group05.nomnomathon.converter;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.*;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantData;

import java.util.ArrayList;


/**
 * Created by Agnes on 31.05.16.
 */
@Component
public class RestaurantDataConverter {


    public void process(Exchange exchange) {
        BasicDBObject objectRestaurant = exchange.getIn().getBody(BasicDBObject.class);
        RestaurantData data = new RestaurantData();
        data.set_id((int) objectRestaurant.get("_id"));
        data.setLocation((String) objectRestaurant.get("location"));
        data.setOpening((String) objectRestaurant.get("opening"));
        data.setClosing((String) objectRestaurant.get("closing"));
        ArrayList<String> cats = new ArrayList<>();
        BasicDBList catsList = (BasicDBList) objectRestaurant.get("categories");
        for (Object o : catsList) {
            cats.add((String) o);
        }
        data.setCats(cats);
        ArrayList<Menu> menus = new ArrayList<>();
        ArrayList<BasicDBObject> menuDbList = (ArrayList<BasicDBObject>) objectRestaurant.get("menu");

        for (BasicDBObject o : menuDbList) {
            Menu menu = new Menu();
            menu.setName((String) o.get("name"));
            menu.setPrice((double) o.get("price"));
            menus.add(menu);
        }
        data.setMenu(menus);
        exchange.getIn().setBody(data);
    }

}
