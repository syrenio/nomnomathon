package wmpm16.group05.nomnomathon.beans.translators;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("jsonToRestaurantDataTranslator")
public class XmlToRestaurantData implements Processor {

	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		System.err.println(exchange.getIn().getBody());

	}

}
