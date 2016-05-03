package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.beans.translators.CsvToRestaurantData;
import wmpm16.group05.nomnomathon.beans.translators.JsonToRestaurantData;
import wmpm16.group05.nomnomathon.beans.translators.XmlToRestaurantData;

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
	@Value("${restaurantUpdate.dbName}")
	private String dbName;
	@Value("${restaurantUpdate.collectionName}")
	private String collectionName;
	private String inputFolderPath;

	@Override
	public void configure() throws Exception {
		inputFolderPath = System.getProperty("user.dir") + "/" + inputFolder;


		from("file:" + inputFolderPath + "?consumer.delay=" + delay + "&charset=utf-8&noop=" + keepFiles).
			choice()
				.when().simple("${file:name.ext} == 'csv'").unmarshal().csv().process(csvToRestaurantDataTranslator)
				.when().simple("${file:name.ext} == 'xml'").unmarshal().jacksonxml().process(xmlToRestaurantDataTranslator)
				.when().simple("${file:name.ext} == 'json'").unmarshal().json(JsonLibrary.Gson).process(jsonToRestaurantDataTranslator)
			.to("mongodb:mongoDb?database="+dbName+"&collection="+collectionName+"&operation=insert");
	}
}