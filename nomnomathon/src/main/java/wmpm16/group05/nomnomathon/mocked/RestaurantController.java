package wmpm16.group05.nomnomathon.mocked;

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

    /*Reply the restaurant's id if there is capacity, and -1 if not */
    @RequestMapping(value = "/{id}/capacity",  method = RequestMethod.GET)
    public @ResponseBody Long hasCapacity(@PathVariable Long id) {
        return Math.random() < 0.9? id : -1;
        }
   

    @RequestMapping(value = "/{id}/order",  method = RequestMethod.POST)
    @ResponseBody
    public OrderRequestAnswer sendOrder(@PathVariable String id, @RequestBody OrderInProcess order) {
        return new OrderRequestAnswer(Math.random()<0.9,Long.valueOf(id), order.getOrderId());
    }
    

}
