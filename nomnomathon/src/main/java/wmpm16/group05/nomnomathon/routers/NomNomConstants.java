package wmpm16.group05.nomnomathon.routers;

public class NomNomConstants {

    public static final String HEADER_RESTAURANT_ID = "restaurantId";
    public static final String HEADER_DISHES_ORDER = "dishesOrder";
    public static final String HEADER_DISHES_PRICES = "dishesPrices";
    public static final String HEADER_AMOUNT = "amount";
    public static final String HEADER_ORDER_STATE = "orderState";
    public static final String HEADER_RESTAURANTS = "restaurants";
    public static final String HEADER_CREDIT_CARD = "creditCard";

    public static final String HEADER_NOTIFICATION_TYPE = "notificationType";
    public static final String HEADER_FIRST_NAME = "firstName";
    public static final String HEADER_LAST_NAME = "lastName";
    public static final String HEADER_TYPE = "type";
    public static final String HEADER_DISH_NAMES = "dishNames";
    public static final String HEADER_ORDER_ID = "orderId";
    public static final String HEADER_CUSTOMER_ID = "customerId";

    public static final Long AGGREGATION_TIMEOUT = 1000L;
    public static final Long THROTTLER_PERIOD = 10000L;
    
    // camel components
    public static final String HEADER_SMTP_TO = "to";
    public static final String HEADER_SUBJECT = "subject";
    public static final String HEADER_SMPP_PHONENUMBER = "CamelSmppDestAddr";

    
}
