package wmpm16.group05.nomnomathon.beans.translators;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("csvToRestaurantDataTranslator")
public class CsvToRestaurantData implements Processor{
	
	public static final Logger logger = Logger.getLogger(CsvToRestaurantData.class);
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		//System.err.println(exchange.getIn().getBody().getClass());
		logger.error(exchange.getIn().getBody().getClass());
		
	}

}
