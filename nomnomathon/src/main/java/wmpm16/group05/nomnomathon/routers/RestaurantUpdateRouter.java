package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.beans.translators.JsonToRestaurantData;
import wmpm16.group05.nomnomathon.beans.translators.XmlToRestaurantData;
import wmpm16.group05.nomnomathon.beans.translators.CsvToRestaurantData;

/**
 * RestaurantUpdateRouter using the Normalizer Pattern
 * 
 * @author till
 */
@Component
public class RestaurantUpdateRouter extends RouteBuilder {

	@Autowired
	private CsvToRestaurantData csvToRestaurantDataTranslator;
	@Autowired
	private XmlToRestaurantData xmlToRestaurantDataTranslator;
	@Autowired
	private JsonToRestaurantData jsonToRestaurantDataTranslator;
	@Value("${restaurantUpdate.inputFolder}")
	private String inputFolder;
	@Value("${restaurantUpdate.delay}")
	private String delay;
	@Value("${restaurantUpdate.keepFiles}")
	private String keepFiles;
	private String inputFolderPath;

	@Override
	public void configure() throws Exception {
		inputFolderPath = System.getProperty("user.dir") + "/" + inputFolder;

		configureREST();

		from("file:" + inputFolderPath + "?consumer.delay=" + delay + "&charset=utf-8&noop=" +keepFiles).
			choice()
				.when().simple("${file:name.ext} == 'csv'").unmarshal().csv().process(csvToRestaurantDataTranslator)
				.when().simple("${file:name.ext} == 'xml'").unmarshal().jacksonxml().process(xmlToRestaurantDataTranslator)
				.when().simple("${file:name.ext} == 'json'").unmarshal().json(JsonLibrary.Gson).process(jsonToRestaurantDataTranslator)
			.to("log:mock:result");
	}

	private void configureREST(){
		restConfiguration().component("servlet").bindingMode(RestBindingMode.json);
		rest("/")
				.get("/status").to("direct:status");
		from("direct:status")
				.transform().constant("running!");
	}
}