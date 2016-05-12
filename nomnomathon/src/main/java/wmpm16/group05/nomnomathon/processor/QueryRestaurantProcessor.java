package wmpm16.group05.nomnomathon.processor;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agnes on 12.05.16.
 */
@Component
public class QueryRestaurantProcessor implements Processor {


    @Override
    public void process(Exchange exchange) throws Exception {
        // System.out.println("findAll: " + exchange.getIn().getBody(ArrayList.class));
        List<BasicDBObject> result = exchange.getIn().getBody(ArrayList.class);
        System.out.println("results: " + result.size() + " restaurant/s found");
        BasicDBObject object = result.get(0);
        BasicDBList dishes = (BasicDBList) object.get("menu");
        System.out.println("menu: " + dishes.size() + " dishes found: " + dishes);
        BasicDBObject firstDish = (BasicDBObject) dishes.get(0);
        System.out.println("first dish: " + firstDish.get("NAME"));
    }
}
