package wmpm16.group05.nomnomathon.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Menu {
	@NotNull
	@Size(min = 2, max = 150)
	private String name;
	@NotNull
	@Min(0)
	private double price;
	
	public Menu() {}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
}
