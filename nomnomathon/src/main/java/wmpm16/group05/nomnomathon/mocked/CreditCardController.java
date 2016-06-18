package wmpm16.group05.nomnomathon.mocked;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Created by syrenio on 04/05/16.
 */
@RestController
@RequestMapping("/external/creditcards")
public class CreditCardController {

	private final Logger log = Logger.getLogger(CreditCardController.class);

	@RequestMapping(value = "/{creditCard}", method = RequestMethod.GET)
	@ResponseBody
	public PaymentRequestAnswer getRestaurant(@PathVariable String creditCard, @RequestParam(value = "amount") BigDecimal amount) {
		log.debug("Received payment request for CreditCard " + creditCard + " and amount " + amount);
		double rnd = Math.random();
		if("6011671952963165".equals(creditCard)){
			return new PaymentRequestAnswer(false);
		}
		return new PaymentRequestAnswer(true); //rnd < 0.5
	}
}