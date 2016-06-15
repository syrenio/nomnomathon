package wmpm16.group05.nomnomathon.beans;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.routers.NomNomConstants;
import wmpm16.group05.nomnomathon.routers.RESTRouter;

public class MatchingRestaurantsToReceipientListProcessor implements Processor {
	Logger log = Logger.getLogger(MatchingRestaurantsToReceipientListProcessor.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		Long requestid = NomNomConstants.REQUESTCOUNTER.get();
		int size = exchange.getIn().getBody(List.class).stream()
				.filter(o -> o instanceof RestaurantData).mapToInt(o -> 1).sum();
		String recipients = exchange.getIn().getBody(List.class).stream()
				.filter(o -> o instanceof RestaurantData)
				.map(rd -> "http://localhost:8080/external/restaurants/" +
						String.valueOf(((RestaurantData)rd).get_id()) + "/" +
						String.valueOf(requestid) + "/capacity")
				.collect(Collectors.joining(",")).toString();
		log.debug("Recipients String: " + recipients);
		exchange.getIn().setHeader(NomNomConstants.MATCHING_RESTAURANTS, recipients);
		exchange.getIn().setHeader(NomNomConstants.MATCHING_RESTAURANTS_SIZE, size);
		exchange.getIn().setHeader(NomNomConstants.MATCHING_REQUEST, requestid);
		
	}
};

