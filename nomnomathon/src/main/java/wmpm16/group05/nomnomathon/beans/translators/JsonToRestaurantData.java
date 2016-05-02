package wmpm16.group05.nomnomathon.beans.translators;

import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import wmpm16.group05.nomnomathon.models.RestaurantDataModel;

@Component("xmlToRestaurantDataTranslator")
public class JsonToRestaurantData extends RestaurantDataValidator implements Processor {

	private Gson gson = new Gson();
	@Autowired 
	private RestaurantDataValidator validator;
	
	public void process(Exchange exchange) throws Exception {		
		
		LinkedTreeMap<?, ?> restaurantData = (LinkedTreeMap<?, ?>) exchange.getIn().getBody();
		LinkedTreeMap<?, ?> data = (LinkedTreeMap<?, ?>) restaurantData.get("DATA");
		ArrayList<String> menu = (ArrayList<String>) restaurantData.get("MENU");
			
		String name = (String) data.get("NAME");
		String loc = (String) data.get("LOCATION");
		String opening = (String) data.get("OPENING");
		String closing = (String) data.get("CLOSING");
		ArrayList<String> cats = (ArrayList<String>) data.get("CATEGORIES");	
		
		if(validator.validateData(name, loc, opening, closing, cats, menu)){
			//Update Message Body to our valid json
			exchange.getIn().setBody(gson.toJson(new RestaurantDataModel(name,loc,opening,closing,cats,menu)));
		}else{
			throw new TranslatorException("Provided data seem not to be valid.");
		}
	}

}
