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

import wmpm16.group05.nomnomathon.database.DatabaseSeeder;
import wmpm16.group05.nomnomathon.models.Customer;
import wmpm16.group05.nomnomathon.models.CustomerNotificationType;
import wmpm16.group05.nomnomathon.models.CustomerRepository;
import wmpm16.group05.nomnomathon.models.DishRepository;
import wmpm16.group05.nomnomathon.models.OrderInProcessRepository;

@Configuration
public class NomNomathonConfig {
	private static final Logger log = Logger.getLogger(NomNomathon.class);
	
    private static final String CAMEL_URL_MAPPING = "/api/*";
    private static final String CAMEL_SERVLET_NAME = "CamelServlet";

    @Autowired
    private DatabaseSeeder databaseSeeder;

    @Value("${mongoDB.host}")
    private String mongoHost;

    
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
        databaseSeeder.seed();
    }
}