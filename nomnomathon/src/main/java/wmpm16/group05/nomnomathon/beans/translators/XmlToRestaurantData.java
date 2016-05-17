package wmpm16.group05.nomnomathon.beans.translators;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("jsonToRestaurantDataTranslator")
public class XmlToRestaurantData implements Processor {
	public static final Logger logger = Logger.getLogger(XmlToRestaurantData.class);
	
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		//System.err.println(exchange.getIn().getBody().getClass());

	}

}
