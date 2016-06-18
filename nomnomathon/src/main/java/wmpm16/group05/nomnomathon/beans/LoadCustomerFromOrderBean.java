package wmpm16.group05.nomnomathon.beans;

import java.util.Optional;

import org.apache.camel.language.Simple;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;

public class LoadCustomerFromOrderBean {
	
	private final Logger log = Logger.getLogger(LoadCustomerFromOrderBean.class);
	
	@Autowired
	CustomerRepository customerRepository;
	
	public Customer findCustomer(@Simple("body.userId") Optional<Long> userid) {
		log.debug("Receive user with id " + userid.get());
		return customerRepository.findOneById(userid.get());
	}

}
