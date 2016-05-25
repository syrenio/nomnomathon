package wmpm16.group05.nomnomathon.beans.translators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.domain.Menu;
import wmpm16.group05.nomnomathon.domain.RestaurantData;

@Component("ConvertToResDataBean")

public class ResDataConverter {
	private List<String> dataList;
	private ArrayList<String> categories;
	private ArrayList<Menu> menuItems = new ArrayList<Menu>();

	public RestaurantData convert(List<List<String>> resDataCsv) {
		RestaurantData resData = new RestaurantData();
		for (List<String> row : resDataCsv) {
			dataList = row.subList(0, 5);
			Menu item = new Menu();
			item.setName(row.get(row.size() - 2));
			item.setPrice(Float.parseFloat(row.get(row.size() - 1)));
			menuItems.add(item);
			categories = new ArrayList<String>(row.subList(5, row.size() - 2));
		}
		resData.setName(dataList.get(0));
		resData.set_id(Integer.parseInt(dataList.get(1).replaceAll("\\s", "")));
		resData.setLocation(dataList.get(2));
		resData.setOpening(dataList.get(3));
		resData.setClosing(dataList.get(3));
		resData.setCats((ArrayList<String>) categories);
		resData.setMenu((ArrayList<Menu>) menuItems);
		return resData;
	}
}