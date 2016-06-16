package wmpm16.group05.nomnomathon.mocked;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    public @ResponseBody Long hasCapacity(@PathVariable Long id) {
    	Boolean answer = Math.random() < 0.9;
    	log.debug("Received a capacity request for ID " + id + " and have capacity left? " + answer);
    	return answer? id : -1;
        }
   

    @RequestMapping(value = "/{id}/order",  method = RequestMethod.POST)
    @ResponseBody
    public OrderRequestAnswer sendOrder(@PathVariable String id, @RequestBody OrderInProcess order) {
    	log.debug("Received a order with restaurantId " + id + " and order " + order);
        return new OrderRequestAnswer(Math.random()<0.9,Long.valueOf(id), order.getOrderId());
    }
    

}
