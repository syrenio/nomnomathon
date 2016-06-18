package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Agnes on 14.06.16.
 */
@Component
public class TransformRestaurantHeaderBean {

    String uri = "http://localhost:8080/external/restaurants/%s/capacity";

    public void process(Exchange exchange) {
        ArrayList<String> restaurantIds = exchange.getIn().getHeader(NomNomConstants.HEADER_RESTAURANTS, ArrayList.class);
        List<String> restaurantIdTransformed = restaurantIds.stream().map(s -> String.format(uri, s)).collect(Collectors.toList());

        exchange.getIn().setHeader(NomNomConstants.HEADER_RESTAURANTS, restaurantIdTransformed);
    }
}
