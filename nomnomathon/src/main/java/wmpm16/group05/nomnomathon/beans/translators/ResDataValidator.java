package wmpm16.group05.nomnomathon.beans.translators;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.domain.RestaurantData;

@Component("resDataValidator")
public class ResDataValidator {
	public void process(Exchange exchange) throws TranslatorException {
		// Do some proper checking for valid data here :-)
		RestaurantData data = (RestaurantData) exchange.getIn().getBody();
		if (!(data.getName() != null && data.getName().length() > 2 && data.getName().length() < 50)
				&& (data.getLocation() != null && data.getLocation().length() == 4)
				&& (data.getOpening() != null && data.getOpening().length() == 5)
				&& (data.getClosing() != null && data.getClosing().length() == 5)
				&& (data.getCategories() != null && data.getCategories().size() > 0)
				&& (data.getMenu() != null && data.getMenu().size() > 0)) {
			throw new TranslatorException("Provided data seem not to be valid.");
		}
	}
}
