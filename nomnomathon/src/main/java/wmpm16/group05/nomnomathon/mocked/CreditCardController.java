package wmpm16.group05.nomnomathon.mocked;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by syrenio on 04/05/16.
 */
@RestController
@RequestMapping("/external/creditcard")
public class CreditCardController {

	private final Logger log = Logger.getLogger(CreditCardController.class);

	@RequestMapping(value = "/{acquirerid}/payment/{userid}/{amount}", method = RequestMethod.GET)
	@ResponseBody
	public PaymentRequestAnswer getRestaurant(@PathVariable String acquirerid, @PathVariable Long userid, @PathVariable Float amount) {
		log.debug("Received payment request with Acquirerid " + acquirerid + " userid " + userid + " and amount " + amount);
		double rnd = Math.random();
		return rnd < 0.5 ? PaymentRequestAnswer.accepted : PaymentRequestAnswer.rejected; 
	}
}
