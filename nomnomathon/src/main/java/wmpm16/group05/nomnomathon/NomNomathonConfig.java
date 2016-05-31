package wmpm16.group05.nomnomathon;

import javax.annotation.PostConstruct;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.*;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerNotificationType;
import wmpm16.group05.nomnomathon.models.CustomerRepository;
import wmpm16.group05.nomnomathon.models.DishRepository;
import wmpm16.group05.nomnomathon.models.OrderRepository;

@Configuration
public class NomNomathonConfig {
	private static final Logger log = Logger.getLogger(NomNomathon.class);
	
    private static final String CAMEL_URL_MAPPING = "/api/*";
    private static final String CAMEL_SERVLET_NAME = "CamelServlet";

    @Value("${mongoDB.host}")
    private String mongoHost;

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    DishRepository dishRepository;
    @Autowired
    OrderRepository orderRepository;
    //@Autowired
    //OrderListEntryRepository orderListEntryRepository;
    
    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        ServletRegistrationBean registration =
                new ServletRegistrationBean(new CamelHttpTransportServlet(), CAMEL_URL_MAPPING);
        registration.setName(CAMEL_SERVLET_NAME);
        return registration;
    }
    
    @Bean
    public Mongo mongoDb(){
    	log.debug("Resolved Adress for MongoDB: " + this.mongoHost);
    	return new MongoClient(this.mongoHost);
    }
    
    @PostConstruct
    public void initDB(){
        System.out.println("<--- INIT DB STUFF --->");

        customerRepository.deleteAll();
        dishRepository.deleteAll();
        orderRepository.deleteAll();
        //orderListEntryRepository.deleteAll();

        //authcode: YmVybmQ6bm9tbm9t
        Customer customer = new Customer("bernd","bernd","test","nomnom");
        customer.setPhoneNumber("+4368012345678");
        customer.setMail("a.b@c.d");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("Percostraße 27, 1220 Wien");
        customerRepository.save(customer);
        
        //authcode: Ym11OjEyMzQ1
        customer = new Customer("bmu","Bernhard","Müller","12345");
        customer.setPhoneNumber("+4369981259747");
        customer.setMail("bernhard.mueller@gmx.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("Percostraße 25, 1220 Wien");
        customerRepository.save(customer);
        
        //authcode: bWF3ZToxMjM0NQ==
        customer = new Customer("mawe","Martin","Weik","12345");
        customer.setPhoneNumber("+4369912345678");
        customer.setMail("martin@weik.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("blabla, 1234 Wien");
        customerRepository.save(customer);
    }
}