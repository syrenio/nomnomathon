package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.exceptions.BeanValidationHandler;
import wmpm16.group05.nomnomathon.exceptions.IllegalArgumentHandler;

/**
 * RestaurantUpdateRouter using the Normalizer Pattern
 * 
 * @author till
 */
@Component
public class RestaurantUpdateRouter extends RouteBuilder {

	@Value("${restaurantUpdate.inputFolder}")
	private String inputFolder;
	@Value("${restaurantUpdate.delay}")
	private String delay;
	@Value("${restaurantUpdate.keepFiles}")
	private String keepFiles;
	@Value("${restaurantUpdate.dbName}")
	private String dbName;
	@Value("${restaurantUpdate.collectionName}")
	private String collectionName;
	private String inputFolderPath;
	private JacksonDataFormat restaurantjsonformat;
	private JacksonXMLDataFormat restaurantxmlformat;

	@Override
	public void configure() throws Exception {
		restaurantjsonformat = new JacksonDataFormat();
		restaurantxmlformat = new JacksonXMLDataFormat();
		inputFolderPath = System.getProperty("user.dir") + "/" + inputFolder;
		restaurantjsonformat.setUnmarshalType(RestaurantData.class);
		restaurantxmlformat.setUnmarshalType(RestaurantData.class);
		
		 /* GLOBAL Error handler on exception */
        onException(BeanValidationException.class).handled(true).bean(BeanValidationHandler.class);
        onException(IllegalArgumentException.class).handled(true).bean(IllegalArgumentHandler.class);


		/* Providing REST Endpoint for Restaurant Data Updates */
		rest("/").bindingMode(RestBindingMode.off).
				post("/updateResData").to("direct:resUpdate");

		/* File scan Endpoint for Restaurant Data Updates */
		from("file:" + inputFolderPath + "?consumer.delay=" + delay + "&charset=utf-8&noop=" + keepFiles)
				.to("direct:resUpdate");

		/*
		 * Message Router in front of a number of Message Translator instances
		 */
		from("direct:resUpdate")
				.to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=DEBUG&marker=loaded")
				.choice()

					/* CSV */
					 .when().simple("${file:name.ext} == 'csv' || ${in.headers.Content-Type} == 'text/csv'").unmarshal()
					 .csv().bean("ConvertToResDataBean")
	
					/* XML */
					.when().simple("${file:name.ext} == 'xml' || ${in.headers.Content-Type} == 'text/xml'")
						.unmarshal(restaurantxmlformat).to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=DEBUG")
	
					/* JSON */
					.when().simple("${file:name.ext} == 'json' || ${in.headers.Content-Type} == 'application/json'").to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=DEBUG")
						.unmarshal(restaurantjsonformat).to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=DEBUG")
					
					/* Otherwise */	
					.otherwise().throwException(new IllegalArgumentException("Dataformat not supported")).stop()	
				.end()

				/* Validate */
				.to("bean-validator://x")

				/* Update Mongo DB */
				.to("mongodb:mongoDb?database=" + dbName + "&collection=" + collectionName + "&operation=save")
				.to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=INFO&marker=saved");
	}
}