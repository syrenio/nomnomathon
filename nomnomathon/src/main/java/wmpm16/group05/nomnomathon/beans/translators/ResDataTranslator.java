package wmpm16.group05.nomnomathon.beans.translators;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import wmpm16.group05.nomnomathon.domain.RestaurantData;

@Component("resDataTranslator")

public class ResDataTranslator {
	private Gson gson = new Gson();
	@Autowired 
	private RestaurantDataValidator validator;
	public void transJson(Exchange exchange) throws TranslatorException{
		HashMap<?, ?> body = (HashMap<?, ?>) exchange.getIn().getBody();
		HashMap<?, ?> resdata = (HashMap<?, ?>) body.get("restaurantdata");
		HashMap<?, ?> data = (HashMap<?, ?>) resdata.get("data");
		ArrayList<String> menu = (ArrayList<String>) resdata.get("menu");
		Integer id  = (Integer) data.get("_id");
		String name = (String) data.get("name");
		String loc = (String) data.get("location");
		String opening = (String) data.get("opening");
		String closing = (String) data.get("closing");
		ArrayList<String> cats = (ArrayList<String>) data.get("categories");	
		
		if(validator.validateData(name, loc, opening, closing, cats, menu)){
			//Update Message Body to our valid json
			exchange.getIn().setBody((new RestaurantData(id, name,loc,opening,closing,cats,menu)));
		}else{
			throw new TranslatorException("Provided data seem not to be valid.");
		}
	}
	
	public void transXml(Exchange exchange){
		
		
	}
	
	public void transCsv(Exchange exchange){
		
	}

}
