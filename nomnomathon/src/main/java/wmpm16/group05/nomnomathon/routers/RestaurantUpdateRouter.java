package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
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
	private String inputFolderPath;

	@Override
	public void configure() throws Exception {
		inputFolderPath = System.getProperty("user.dir") + "/" + inputFolder;
		from("file:" + inputFolderPath + "?consumer.delay=" + delay).
			choice()
				.when().simple("${file:name.ext} == 'csv'").unmarshal().csv().process(csvToRestaurantDataTranslator)
				.when().simple("${file:name.ext} == 'xml'").unmarshal().jacksonxml().process(xmlToRestaurantDataTranslator)
				.when().simple("${file:name.ext} == 'json'").unmarshal().json(JsonLibrary.Jackson).process(jsonToRestaurantDataTranslator)
			.marshal().json(JsonLibrary.Jackson)
			.to("log:mock:result");
	}

}