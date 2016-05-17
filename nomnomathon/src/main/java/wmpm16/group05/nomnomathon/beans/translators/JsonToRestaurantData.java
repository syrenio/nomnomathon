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
public class JsonToRestaurantData implements Processor {

	private Gson gson = new Gson();
	@Autowired 
	private RestaurantDataValidator validator;
	
	public void process(Exchange exchange) throws Exception {		
		
		LinkedTreeMap<?, ?> body = (LinkedTreeMap<?, ?>) exchange.getIn().getBody();
		LinkedTreeMap<?, ?> resdata = (LinkedTreeMap<?, ?>) body.get("restaurantdata");
		LinkedTreeMap<?, ?> data = (LinkedTreeMap<?, ?>) resdata.get("data");
		ArrayList<String> menu = (ArrayList<String>) resdata.get("menu");
		Integer id  = ((Double) data.get("id")).intValue();
		String name = (String) data.get("name");
		String loc = (String) data.get("location");
		String opening = (String) data.get("opening");
		String closing = (String) data.get("closing");
		ArrayList<String> cats = (ArrayList<String>) data.get("categories");	
		
		if(validator.validateData(name, loc, opening, closing, cats, menu)){
			//Update Message Body to our valid json
			exchange.getIn().setBody(gson.toJson(new RestaurantDataModel(id, name,loc,opening,closing,cats,menu)));
		}else{
			throw new TranslatorException("Provided data seem not to be valid.");
		}
	}

}
