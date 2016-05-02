package wmpm16.group05.nomnomathon.models;

import java.util.ArrayList;

public class RestaurantDataModel {
	private String name;
	private String loc;
	private String open;
	private String close;
	private ArrayList<String> cats;
	private ArrayList<String> menu;
	
	
	public RestaurantDataModel(String name, String loc, String opening, String closing, ArrayList<String> cats, ArrayList<String> menu){
		this.name = name;
		this.loc = loc;
		this.open = opening;
		this.close = closing;
		this.cats = cats;
		this.menu = menu;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLoc() {
		return loc;
	}


	public void setLoc(String loc) {
		this.loc = loc;
	}


	public String getOpening() {
		return open;
	}


	public void setOpening(String opening) {
		this.open = opening;
	}


	public String getClosing() {
		return close;
	}


	public void setClosing(String closing) {
		this.close = closing;
	}


	public ArrayList<String> getCats() {
		return cats;
	}


	public void setCats(ArrayList<String> cats) {
		this.cats = cats;
	}


	public ArrayList<String> getMenu() {
		return menu;
	}


	public void setMenu(ArrayList<String> menu) {
		this.menu = menu;
	}
}
