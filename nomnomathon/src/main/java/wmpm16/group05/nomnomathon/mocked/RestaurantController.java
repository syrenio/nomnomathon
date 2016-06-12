package wmpm16.group05.nomnomathon.mocked;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import wmpm16.group05.nomnomathon.mocked.OrderRequestAnswer.OrderRequestState;
import wmpm16.group05.nomnomathon.models.OrderInProcess;


/**
 * Created by syrenio on 04/05/16.
 */
@RestController
@RequestMapping("/external/restaurants")
public class RestaurantController {

    private final Logger log = Logger.getLogger(RestaurantController.class);
    /*Reply the restaurant's id if there is capacity, and -1 if not */
    @RequestMapping(value = "/{id}/capacity",  method = RequestMethod.GET)
    public @ResponseBody Long isAvailable(@PathVariable Long id) {
    	id = Math.random() > 0.5? id : -1;
        return id;
        }

    @RequestMapping(value = "/{id}/order",  method = RequestMethod.POST)
    @ResponseBody
    public OrderRequestAnswer sendOrder(@PathVariable String id, @RequestBody OrderInProcess order) {
        log.debug("Received order with restaurantid " + id + " and order " + order);
        double rnd = Math.random();
        OrderRequestState answer = rnd < 0.5 ? OrderRequestState.accepted : OrderRequestState.rejected;
        return new OrderRequestAnswer(answer,Long.valueOf(id), order.getOrderId());
    }
    

}
