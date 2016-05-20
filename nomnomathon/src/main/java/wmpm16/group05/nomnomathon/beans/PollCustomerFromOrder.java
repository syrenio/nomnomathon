package wmpm16.group05.nomnomathon.beans;

import org.apache.camel.language.Simple;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;

public class PollCustomerFromOrder {
	
	private final Logger log = Logger.getLogger(PollCustomerFromOrder.class);
	
	@Autowired
	CustomerRepository customerRepository;
	
	public Customer findCustomer(@Simple("body.userid") long userid) {
		log.debug("Receive user with id " + userid);
		return customerRepository.findOneById(userid);
	}

}
