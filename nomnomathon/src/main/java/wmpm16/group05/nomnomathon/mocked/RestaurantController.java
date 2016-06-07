package wmpm16.group05.nomnomathon.mocked;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;
import wmpm16.group05.nomnomathon.mocked.OrderRequestAnswer.OrderRequestState;
import wmpm16.group05.nomnomathon.models.OrderInProcess;


/**
 * Created by syrenio on 04/05/16.
 */
@RestController
@RequestMapping("/external/restaurants")
public class RestaurantController {

	private final Logger log = Logger.getLogger(RestaurantController.class);

	@RequestMapping(value = "/{id}/{requestid}/capacity", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public RestaurantCapacityResponse getRestaurant(@PathVariable String id, @PathVariable String requestid) {
		log.debug("Received status request with restaurantid " + id);
		double rnd = Math.random();
		RestaurantCapacityResponse resp = new RestaurantCapacityResponse(Long.valueOf(id), Long.valueOf(requestid));
		resp.setCapacityAvailable(rnd > 0.5);
		return resp;
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
