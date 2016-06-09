package wmpm16.group05.nomnomathon.routers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.aggregation.CapacityAggregationStrategy;
import wmpm16.group05.nomnomathon.aggregation.DishesOrderAggregation;
import wmpm16.group05.nomnomathon.aggregation.EnrichCustomer;
import wmpm16.group05.nomnomathon.aggregation.RestaurantDataAggregation;
import wmpm16.group05.nomnomathon.beans.*;
import wmpm16.group05.nomnomathon.converter.DBObjectToResDataConverter;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;
import wmpm16.group05.nomnomathon.exceptions.InvalidFormatHandler;
import wmpm16.group05.nomnomathon.exceptions.UnrecognizedPropertyHandler;
import wmpm16.group05.nomnomathon.mocked.OrderRequestAnswer;
import wmpm16.group05.nomnomathon.mocked.PaymentRequestAnswer;
import wmpm16.group05.nomnomathon.models.Dish;
import wmpm16.group05.nomnomathon.models.OrderInProcess;
import wmpm16.group05.nomnomathon.models.OrderState;



/**
 * Created by syrenio on 5/3/2016.
 */
@Component
public class RESTRouter extends RouteBuilder {

	public static final String MATCHING_RESTAURANTS_SIZE = "MATCHING_RESTAURANTS_SIZE";
	public static final String MATCHING_RESTAURANTS = "matching-restaurants";
	public static final String MATCHING_REQUEST = "REQUESTID";
	//THIS IS JUST FOR TESTPURPOSE
	public static final AtomicLong REQUESTCOUNTER = new AtomicLong();
	public static final String HEADER_RESTAURANT_ID = "restaurantId";
	public static final String HEADER_DISHES_ORDER = "dishesOrder";
	public static final String HEADER_DISHES_PRICES = "dishesPrices";
	public static final String HEADER_AMOUNT = "amount";

	@Override
    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        /* GLOBAL Error handler on exception */
        onException(UnrecognizedPropertyException.class).handled(true).bean(UnrecognizedPropertyHandler.class);
        onException(InvalidFormatException.class).handled(true).bean(InvalidFormatHandler.class);


        rest("/")
                .get("/status").to("direct:status")
                .post("/orders").type(OrderRequest.class).to("direct:postOrder");


        /*REST Endpoint to check if service is running*/
        from("direct:status")
                .transform().constant("running!");




        /*Start of the process*/
        from("direct:postOrder")
                .choice()
                .when(exchange -> exchange.getIn().getBody(OrderRequest.class).getType() == OrderType.SMS)
                .to("direct:postOrderWithSMS")
                .when(exchange -> exchange.getIn().getBody(OrderRequest.class).getType() == OrderType.REGULAR)
                .to("direct:postOrderWithREGULAR")
                .end();

        /*
        * check if SMS order is of type SMS and contains keyword 'hungry'
        * perform authentication and extract customerId
        * */
        from("direct:postOrderWithSMS")
                .filter(simple("${in.body.type} == 'SMS' && ${in.body.text} contains 'hungry'")) /*IGNORE OTHER Messages */
                .bean(SMSAuthBean.class)
                .filter(simple("${in.body.userId.present} == true"))
                .to("direct:enrichCustomerData")
                .end()
                .end();

        /*extract customerId from header */
        from("direct:postOrderWithREGULAR")
                .bean(RegularAuthBean.class)
                .filter(simple("${in.body.userId.present} == true"))
                .to("direct:enrichCustomerData")
                .end();

        /*enrich order-request with customer data from DB and transform to Order*/
        from("direct:enrichCustomerData")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.enrichCustomerData?level=DEBUG")
                .enrich("direct:pollUser", new EnrichCustomer())
                .to("direct:storeOrder").end();
        
		/*poll user data from SQL DB*/
        from("direct:pollUser")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.pollUser?level=DEBUG")
                .bean(PollCustomerFromOrder.class);

        /*store order in DB*/
        from("direct:storeOrder")
                .bean(StoreOrderBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.storeOrder.after?level=DEBUG")
                    .choice()
                        .when(header("type").isEqualTo(OrderType.SMS))
                            .to("direct:hungryDish")
                        .otherwise()
                            .to("direct:queryRestaurants")
                    .end();

        from("direct:hungryDish")
                .setBody().simple("{ \"menu.price\": { $gt: 0, $lt: 20 }}")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findAll")
                .to("direct:splitRestaurants");


        /*query restaurants for dishes*/
        from("direct:queryRestaurants")
                .bean(ExtractDishRestaurantBean.class)
                .to("direct:findAll");

        from("direct:findAll")
                .setBody().simple("{\"menu.name\":{ $in: [${header.dishNames}]}}").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.findAll?level=DEBUG")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findAll").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.findAll?level=DEBUG")
                .to("direct:splitRestaurants");


        from("direct:splitRestaurants")
                .split(body())
                    .bean(DBObjectToResDataConverter.class)
                    .aggregate(constant(true), new RestaurantDataAggregation()).completionTimeout(10)
                     //constant ist needed in order to aggregate all messages into a single message
                     //stops aggregation after 10 milliseconds
                    .choice()
                        .when(header("type").isEqualTo(OrderType.SMS))
                            .to("direct:extractHungryDish")
                        .when(header("type").isEqualTo(OrderType.REGULAR))
                            .to("direct:extractRegularDish")
                    .end()
                .end();


        from("direct:extractHungryDish")
                .bean(RandomDishBean.class)
                .to("direct:checkRestaurantsAvailability")
                .end();

        from("direct:extractRegularDish")
                .enrich("direct:pollOrder", new DishesOrderAggregation())
                .bean(QueryRestaurantBean.class)
                .to("direct:checkRestaurantsAvailability")
                .end();


        /*poll order data from SQL DB*/
        from("direct:pollOrder")
                .bean(PollOrder.class);

        /**
         * MAINTAINER: 
         * PRECONDITIONS
         * - BODY
         * -- 
         * - HEADER
         * --
         */
        from("direct:checkRestaurantsAvailability")
                .choice()
                    .when(header("orderState").isEqualTo(OrderState.ENRICHED))
                        .to("direct:requestCapacity")
                    .when((header("orderState").isEqualTo(OrderState.REJECTED_NO_RESTAURANTS)))
                        .to("direct:rejectOrder")
                .end();


        /**
         * ROUTE: direct:requestCapacity
         * PATTERN: SCATTER-GETTER
         * MAINTAINER: MWEIK
         * 
         * PRECONDITIONS
         * - BODY
         * -- A List<RestaurantData> of all MATCHING Restaurants
         * - HEADER
         * -- orderId : Id of the order
         * 
         * POSTCONDITIONS
         * - BODY
         * -- 
         */
 		AggregationStrategy strategy = new CapacityAggregationStrategy();
        from("direct:requestCapacity")
                /* create receipientlist */
        		.process(new MatchingRestaurantsToReceipientListProcessor())
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
				.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
				.setBody(constant(null))
				.recipientList(header(MATCHING_RESTAURANTS)).delimiter(",").parallelProcessing()
				.unmarshal().json(JsonLibrary.Jackson, RestaurantCapacityResponse.class)
				.aggregate(constant(true), strategy).completionTimeout(1000).completionSize(header(MATCHING_RESTAURANTS_SIZE))
				//.aggregationStrategy()
        		/* load order */
        		//.to("direct:pollOrder")
                /* Aggregate */
        		//xpath("/RestaurantCapacityResponse/requestid"
        		.to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.requestCapacity?level=DEBUG")
        		.process(arg0 -> {
                    log.debug(arg0.getProperty("CamelAggregatedCompletedBy", String.class));
                })
        		.to("direct:checkRestaurantAvailable");

        /* TODO If one or more restaurants are available ,  ACCEPT OR REJECT*/
        /**
         * MAINTAINER: 
         * PRECONDITIONS
         * - BODY
         * -- A List<RestaurantData> of AVAILABLE Restaurants
         * - HEADER
         * -- orderId : Id of the order
         * POSTCONDITIONS
         * - BODY
         * -- An OrderInProcess with restaurant an dishes with prices.
         */
        from("direct:checkRestaurantAvailable")
                .choice()
                .when(simple("${in.body.size} > 0"))
					.to("direct:selectBestFitRestaurant")
                .otherwise()
					.to("direct:rejectOrder")
                .end();

        /* TODO select best restaurant for this order*/
        from("direct:selectBestFitRestaurant")
				.process(x->{
					/*FIXME*/
					RestaurantCapacityResponse resp  = (RestaurantCapacityResponse) x.getIn().getBody(ArrayList.class).get(0);
					x.getIn().setHeader(HEADER_RESTAURANT_ID, resp.getRestaurantId());
					log.info("selectBestFitRestaurant " + resp.getRestaurantId() + " out of: " + x.getIn().getBody(ArrayList.class).size());
				})
				.setBody(header(HEADER_RESTAURANT_ID))
				.to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findById&writeResultAsHeader=true")
				.process(x->{
					List<String> dishNames = x.getIn().getHeader(HEADER_DISHES_ORDER,List.class);
					Map<String,Double> dishesPrices = new HashMap<>();
					BasicDBObject rest = x.getIn().getBody(BasicDBObject.class);
					BasicDBList menu = (BasicDBList) rest.get("menu");
					BigDecimal sum = new BigDecimal(0d);
					for (Object entry : menu) {
						BasicDBObject obj = (BasicDBObject) entry;
						if(dishNames.contains(obj.getString("name"))){
							sum = sum.add(BigDecimal.valueOf(obj.getDouble("price")));
							dishesPrices.put(obj.getString("name"),obj.getDouble("price"));
						}
					}
					log.info("selectBestFitRestaurant calculate prices " + sum);
					x.getIn().setHeader(HEADER_AMOUNT, sum.doubleValue());
					x.getIn().setHeader(HEADER_DISHES_PRICES, dishesPrices);
				})
                .to("direct:checkCreditCard");


        /* TODO */
		/**
		 * MAINTAINER: BeLa
		 * PRECONDITIONS
		 * - BODY
		 * --
		 * - HEADER
		 * --creditCard
		 * --amount
		 */
		from("direct:checkCreditCard")
				.bean(PrepareForCreditCheckBean.class)
				.setHeader(Exchange.HTTP_URI, simple("http://localhost:8080/external/creditcards/${header.creditCard}?amount=${header.amount}"))
				.setHeader(Exchange.CONTENT_TYPE, constant(org.springframework.http.MediaType.APPLICATION_JSON_VALUE))
				.setBody(constant(null))
				.to("http://dummyHost")
				.unmarshal().json(JsonLibrary.Jackson,PaymentRequestAnswer.class)
				.choice()
				.when(simple("${in.body.liquid} == true")).to("direct:sendOrderToRestaurant")
				.otherwise().to("direct:rejectOrder")
				.end();


        from("direct:sendOrderToRestaurant")
                /* TODO HTTP */
                .to("direct:updateOrder");

        from("direct:updateOrder")
                /* TODO Update order in db, needs more info*/
				.bean(LoadOrderBean.class)
				.process(x->{
					OrderInProcess order = x.getIn().getBody(OrderInProcess.class);
					order.setRestaurantId(x.getIn().getHeader(HEADER_RESTAURANT_ID, Long.class));
					order.setState(OrderState.FULLFILLED);
					/*set prices for dishes*/
					Map<String, Double> dishPrices = x.getIn().getHeader(HEADER_DISHES_PRICES, Map.class);
					for (Dish dish : order.getDishes()) {
						Double price = dishPrices.getOrDefault(dish.getDish(), 0d);
						dish.setPrice(price);
					}
				})
				.bean(UpdateOrderBean.class)
                .to("direct:finishOrder");

        from("direct:finishOrder")
                /* TODO set stuff for notification*/
                .to("direct:notifyCustomer");

        /*reject order, update DB*/
        from("direct:rejectOrder")
                //TODO update OrderInProcess in DB
                .to("direct:notifyCustomer");

        /*notify customer via channel*/
        from("direct:notifyCustomer")
                .filter(header("orderState").isEqualTo(OrderState.RESTAURANT_SELECT))
                .setHeader("orderState").constant(OrderState.FULLFILLED) // just for DEMO without second part of process
                .end()
                .wireTap("direct:sendCustomerNotification")
                .process(x -> {
                    System.out.println(x.getIn().getBody());
                });

        /* Next processes*/

		from("direct:loadOrder")
				.bean(LoadOrderBean.class)
				.end();

        from("direct:start")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println(exchange.getIn().getBody());
                    }
                })
                .to("http://localhost:8080/external/restaurants/pizzza")
                .unmarshal().json(JsonLibrary.Jackson, RestaurantCapacityResponse.class)
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        RestaurantCapacityResponse response = exchange.getIn().getBody(RestaurantCapacityResponse.class);
                        System.out.println(response);
                    }
                });

    }

}