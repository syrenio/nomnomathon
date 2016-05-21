package wmpm16.group05.nomnomathon.models;

public enum OrderState {
	CREATED, 			// added customer and dish names
	ENRICHED,			// added dish prices and quantities
	RESTAURANT_SELECT,	// selected restaurant
	FULLFILLED,			// order finished
	REJECTED			// rejected or deleted
}
