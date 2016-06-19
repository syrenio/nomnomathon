package wmpm16.group05.nomnomathon.domain;

import java.util.ArrayList;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RestaurantData {
	@NotNull
	@Min(0)
	private int _id;
	@NotNull
	@Size(min = 2, max = 150)
	private String name;
	@NotNull
	@Size(min = 4)
	private String location;
	@NotNull
	@Size(min = 5, max = 5)
	private String opening;
	@NotNull
	@Size(min = 5, max = 5)
	private String closing;
	@NotNull
	@Size(min = 1, max = 20)
	private ArrayList<String> categories;
	@NotNull
	@Size(min = 1)
	private ArrayList<Menu> menu;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String loc) {
		this.location = loc;
	}


	public String getOpening() {
		return opening;
	}


	public void setOpening(String opening) {
		this.opening = opening;
	}


	public String getClosing() {
		return closing;
	}


	public void setClosing(String closing) {
		this.closing = closing;
	}


	public ArrayList<String> getCategories() {
		return categories;
	}


	public void setCats(ArrayList<String> categories) {
		this.categories = categories;
	}




	public ArrayList<Menu> getMenu() {
		return menu;
	}


	public void setMenu(ArrayList<Menu> menu) {
		this.menu = menu;
	}


	public int get_id() {
		return _id;
	}


	public void set_id(int id) {
		this._id = id;
	}

	@Override
	public String toString() {
		return "RestaurantData [_id=" + _id + ", name=" + name + ", location=" + location + ", opening=" + opening
				+ ", closing=" + closing + ", categories=" + categories + ", menu=" + menu + "]";
	}
	
	
}
