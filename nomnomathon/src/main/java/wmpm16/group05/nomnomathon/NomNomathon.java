package wmpm16.group05.nomnomathon;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wmpm16.group05.nomnomathon.models.*;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class NomNomathon {
	private static final Logger log = Logger.getLogger(NomNomathon.class);
	
    private static final String CAMEL_URL_MAPPING = "/api/*";
    private static final String CAMEL_SERVLET_NAME = "CamelServlet";

    @Value("${mongoDB.host}")
    private String mongoHost;

    @Autowired
    CustomerRepository customerRepository;

    //@Autowired
    //OrderListEntryRepository orderListEntryRepository;

    @Autowired
    DishRepository dishRepository;

    @Autowired
    OrderRepository orderRepository;
    
    /**
     * A main method to start
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(NomNomathon.class, args);
    }

    @PostConstruct
    public void initDB(){
        System.out.println("<--- INIT DB STUFF --->");

        customerRepository.deleteAll();
        dishRepository.deleteAll();
        orderRepository.deleteAll();
        //orderListEntryRepository.deleteAll();

        Customer customer = new Customer("bernd","bernd","test","nomnom");
        customer.setPhoneNumber("+4368012345678");
        customer.setMail("a.b@c.d");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("Percostraße 27, 1220 Wien");
        customerRepository.save(customer);
        
        customer = new Customer("bmu","Bernhard","Müller","12345");
        customer.setPhoneNumber("+4369981259747");
        customer.setMail("bernhard.mueller@gmx.at");
        customer.setNotificationType(CustomerNotificationType.MAIL);;
        customer.setAddress("Percostraße 25, 1220 Wien");
        customerRepository.save(customer);
        
        
    }

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
    
}
