package wmpm16.group05.nomnomathon.domain;

import java.util.ArrayList;

public class RestaurantData {
	private int _id;
	private String name;
	private String location;
	private String opening;
	private String closing;
	private ArrayList<String> categories;
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
	

}
