package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.beans.translators.ResDataTranslator;

/**
 * RestaurantUpdateRouter using the Normalizer Pattern
 * 
 * @author till
 */
@Component
public class RestaurantUpdateRouter extends RouteBuilder {

	@Autowired
	private ResDataTranslator resDataTranslator;
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

	@Override
	public void configure() throws Exception {
		inputFolderPath = System.getProperty("user.dir") + "/" + inputFolder;

		/* Providing REST Endpoint for Restaurant Data Updates */
		rest("/").bindingMode(RestBindingMode.off).post("/updateResData").to("direct:resUpdate");

		/* File scan Endpoint for Restaurant Data Updates */
		from("file:" + inputFolderPath + "?consumer.delay=" + delay + "&charset=utf-8&noop=" + keepFiles)
				.to("direct:resUpdate");

		/*  Message Router in front of a number of Message Translator instances */
		from("direct:resUpdate")
				.to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=DEBUG&marker=loaded")
				.choice()
				
				/*  CSV */
//				.when().simple("${file:name.ext} == 'csv' || ${in.headers.Content-Type} == 'text/csv'").unmarshal()
//				.csv().bean("resDataTranslator", "transCsv")
				
				/*  XML */
//				.when().simple("${file:name.ext} == 'xml' || ${in.headers.Content-Type} == 'text/xml'").unmarshal()
//				.jacksonxml().bean("resDataTranslator", "transXml")
				
				/*  JSON */
				.when().simple("${file:name.ext} == 'json' || ${in.headers.Content-Type} == 'application/json'").unmarshal()
				.json(JsonLibrary.Jackson).bean("resDataTranslator", "transJson")
				.to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=DEBUG")
				.otherwise().stop()
				.end()
				
				/* Update Mongo DB */
				.to("mongodb:mongoDb?database=" + dbName + "&collection=" + collectionName + "&operation=save")
				.to("log:wmpm16.group05.nomnomathon.routers.RestaurantUpdateRouter?level=INFO&marker=saved");
	}
}