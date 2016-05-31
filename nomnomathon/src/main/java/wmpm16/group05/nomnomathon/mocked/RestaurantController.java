package wmpm16.group05.nomnomathon.mocked;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;
import wmpm16.group05.nomnomathon.models.OrderInProcess;

import java.io.IOException;

/**
 * Created by syrenio on 04/05/16.
 */
@RestController
public class RestaurantController {

	private final Logger log = Logger.getLogger(RestaurantController.class);

	@RequestMapping(value = "/external/restaurants/{id}/capacity", method = RequestMethod.GET)
	@ResponseBody
	public RestaurantCapacityResponse getRestaurant(@PathVariable String id) {
		log.debug("Received status request with restaurantid " + id);
		double rnd = Math.random();
		RestaurantCapacityResponse resp = new RestaurantCapacityResponse();
		resp.setCapacityAvailable(rnd > 0.5);
		return resp;
	}

	@RequestMapping(value = "/external/restaurants/{id}/order", method = RequestMethod.POST)
	@ResponseBody
	public OrderRequestAnswer sendOrder(@PathVariable String id, @RequestBody String order) {
		log.debug("Received order with restaurantid " + id + " and order " + order);

		ObjectMapper mapper = new ObjectMapper();
		try {
			OrderInProcess orderObj = mapper.readValue(order, OrderInProcess.class);
			log.debug("Successfully mapped to: " + orderObj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.debug("ERRROR Mapping!");
			e.printStackTrace();
		}
		double rnd = Math.random();
		return rnd < 0.5 ? OrderRequestAnswer.accepted : OrderRequestAnswer.rejected;
	}
}
