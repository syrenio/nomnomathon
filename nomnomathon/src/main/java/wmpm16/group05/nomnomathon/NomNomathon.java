package wmpm16.group05.nomnomathon;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerRepository;
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

        Customer customer = new Customer("bernd","bernd","test","nomnom");
        customer.setPhoneNumber("+4368012345678");
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
