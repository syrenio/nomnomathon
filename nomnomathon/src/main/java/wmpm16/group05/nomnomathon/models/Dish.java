package wmpm16.group05.nomnomathon.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Dish {
	@Id
	@GeneratedValue
	private long id;
    private String dishName;
    private double price;
    
	public String getDish() {
		return dishName;
	}
	
	public void setDish(String dish) {
		this.dishName = dish;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return "Dish [dishName=" + dishName  + ", price=" + price + "]";
	}

}
