package wmpm16.group05.nomnomathon.mocked;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

	private final Logger log = Logger.getLogger(CustomerController.class);
	
    /* Receive notification for customer and display */
	@RequestMapping(value="/customer",  method = RequestMethod.POST)
    public @ResponseBody Long notifyCustomer(@RequestBody String body, @RequestParam(value = "id") Long id) {
		log.info("Received notification for customer ID " + id + ": " + body);
    	return 0L;
    }

}
