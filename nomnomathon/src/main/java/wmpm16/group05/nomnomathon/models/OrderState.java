package wmpm16.group05.nomnomathon.models;

public enum OrderState {
	CREATED, 					// added customer and dish names
	ENRICHED,					// added dish prices and quantities
	RESTAURANT_SELECT,			// selected restaurant
	FULLFILLED,					// order finished
	REJECTED_INVALID_CUSTOMER,	// customer not in DB
	REJECTED_NO_RESTAURANTS,	// no restaurants for dishes in order found
	REJECTED_NO_CAPACITY,		// no restaurants with enough capacity found 
	REJECTED_INVALID_PAYMENT	// payment failed
}
