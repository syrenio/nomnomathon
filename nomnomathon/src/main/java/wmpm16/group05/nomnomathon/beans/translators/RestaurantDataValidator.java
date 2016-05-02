package wmpm16.group05.nomnomathon.beans.translators;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class RestaurantDataValidator{
	public Boolean validateData(String name, String loc, String opening, String closing, ArrayList<String> cats, ArrayList<String> menu) {
	// Do some proper checking for valid data here :-)	
		return
				(name != null && name.length() > 2 && name.length() < 50) &&
				(loc != null && loc.length() == 4) &&
				(opening != null && opening.length() == 5) &&
				(closing != null && closing.length() == 5) &&
				(cats != null && cats.size() > 0) &&
				(menu != null && menu.size() > 0);
	}
	
}
