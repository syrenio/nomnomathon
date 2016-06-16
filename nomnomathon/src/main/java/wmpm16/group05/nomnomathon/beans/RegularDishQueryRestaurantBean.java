package wmpm16.group05.nomnomathon.beans;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mail.MailBinding;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderState;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Agnes on 21.05.16.
 */
@Component
public class RegularDishQueryRestaurantBean {

    public void process(Exchange exchange) {
        ArrayList<RestaurantData> restaurants = exchange.getIn().getBody(ArrayList.class);
        List<String> dishesInOrder = exchange.getIn().getHeader(NomNomConstants.HEADER_DISHES_ORDER, ArrayList.class);
        List<String> restaurantIds = new ArrayList<>();
        List<String> dishesToDeliver = new ArrayList<>();
        for (RestaurantData r : restaurants) {

            String id = String.valueOf(r.get_id());
            List<Menu> menu = r.getMenu();
            List<String> dishes = new ArrayList<>();
            for (Menu m : menu) {
                dishes.add(m.getName());
            }

            for (String d : dishesInOrder) {
                if (dishes.contains(d)) {
                    dishesToDeliver.add(d);
                    if (!restaurantIds.contains(id)) {
                        restaurantIds.add(String.valueOf(r.get_id()));
                    }
                } else {
                    exchange.getIn().setHeader(NomNomConstants.HEADER_ORDER_STATE, OrderState.REJECTED_NO_RESTAURANTS);
                }
            }
        }

        if (exchange.getIn().getHeader(NomNomConstants.HEADER_ORDER_STATE) != OrderState.REJECTED_NO_RESTAURANTS) {
            exchange.getIn().setHeader(NomNomConstants.HEADER_ORDER_STATE, OrderState.ENRICHED);
        }
        /*contains all restaurant ids for capacity check*/
        exchange.getIn().setHeader(NomNomConstants.HEADER_RESTAURANTS, restaurantIds);
    }

}