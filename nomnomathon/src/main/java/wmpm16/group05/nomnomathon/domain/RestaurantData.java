package wmpm16.group05.nomnomathon.domain;

import java.util.ArrayList;

public class RestaurantData {
	private int _id;
	private String name;
	private String location;
	private String opening;
	private String closing;
	private ArrayList<String> categories;
	private ArrayList<String> menu;
	
	
	public RestaurantData(int id, String name, String loc, String opening, String closing, ArrayList<String> categories, ArrayList<String> menu){
		this.setId(id);
		this.name = name;
		this.location = loc;
		this.opening = opening;
		this.closing = closing;
		this.categories = categories;
		this.menu = menu;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLoc() {
		return location;
	}


	public void setLoc(String loc) {
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


	public ArrayList<String> getCats() {
		return categories;
	}


	public void setCats(ArrayList<String> cats) {
		this.categories = cats;
	}


	public ArrayList<String> getMenu() {
		return menu;
	}


	public void setMenu(ArrayList<String> menu) {
		this.menu = menu;
	}


	public int getId() {
		return _id;
	}


	public void setId(int id) {
		this._id = id;
	}
}
