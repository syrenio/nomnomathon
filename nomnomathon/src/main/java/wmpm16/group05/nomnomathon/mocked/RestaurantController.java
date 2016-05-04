package wmpm16.group05.nomnomathon.mocked;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import wmpm16.group05.nomnomathon.routers.RestaurantCapacityResponse;

import javax.websocket.server.PathParam;

/**
 * Created by syrenio on 04/05/16.
 */
@RestController
@RequestMapping("/external/restaurants")
public class RestaurantController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantCapacityResponse getRestaurant(@PathParam("id") String id){
        double rnd = Math.random();
        RestaurantCapacityResponse resp = new RestaurantCapacityResponse();
        resp.setCapacityAvailable(rnd > 0.5);
        return resp;
    }

}
