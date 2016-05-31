package wmpm16.group05.nomnomathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NomNomathon {
	/**
	 * A main method to start
	 */
	public static void main(String[] args) {
		ConfigurableApplicationContext app = SpringApplication.run(NomNomathon.class, args);
	}
}
