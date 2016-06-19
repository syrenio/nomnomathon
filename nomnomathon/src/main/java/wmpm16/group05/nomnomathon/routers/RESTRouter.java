package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import wmpm16.group05.nomnomathon.aggregation.CapacityAggregationStrategy;
import wmpm16.group05.nomnomathon.aggregation.DishesOrderAggregation;
import wmpm16.group05.nomnomathon.aggregation.EnrichCustomer;
import wmpm16.group05.nomnomathon.aggregation.RestaurantDataAggregation;
import wmpm16.group05.nomnomathon.beans.ExtractDishRestaurantBean;
import wmpm16.group05.nomnomathon.beans.LoadCustomerFromOrderBean;
import wmpm16.group05.nomnomathon.beans.LoadOrderBean;
import wmpm16.group05.nomnomathon.beans.PrepareForCreditCheckBean;
import wmpm16.group05.nomnomathon.beans.RandomDishBean;
import wmpm16.group05.nomnomathon.beans.RegularAuthBean;
import wmpm16.group05.nomnomathon.beans.RegularDishQueryRestaurantBean;
import wmpm16.group05.nomnomathon.beans.SMSAuthBean;
import wmpm16.group05.nomnomathon.beans.SelectBestFitRestaurantBean;
import wmpm16.group05.nomnomathon.beans.StoreOrderBean;
import wmpm16.group05.nomnomathon.beans.TransformRestaurantHeaderBean;
import wmpm16.group05.nomnomathon.beans.UpdateOrderBean;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
import wmpm16.group05.nomnomathon.exceptions.InvalidFormatHandler;
import wmpm16.group05.nomnomathon.exceptions.UnrecognizedPropertyHandler;
import wmpm16.group05.nomnomathon.mocked.PaymentRequestAnswer;
import wmpm16.group05.nomnomathon.models.OrderState;


/**
 * Created by syrenio on 5/3/2016.
 */
@Component
public class RESTRouter extends RouteBuilder {

    private JacksonDataFormat restaurantjsonformat;

    
    @Override
    public void configure() throws Exception {
        restaurantjsonformat = new JacksonDataFormat();
        restaurantjsonformat.setUnmarshalType(RestaurantData.class);

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


        /* REST Endpoint to check if service is running */
        from("direct:status")
                .transform().constant("running!");

        /* start of the process */
        from("direct:postOrder")
                .choice()
                .when(exchange -> exchange.getIn().getBody(OrderRequest.class).getType() == OrderType.SMS)
                	.to("direct:postOrderWithSMS")
                .when(exchange -> exchange.getIn().getBody(OrderRequest.class).getType() == OrderType.REGULAR)
                	.to("direct:postOrderWithREGULAR")
                .end()
				.choice()
					.when(simple("${header." + NomNomConstants.HEADER_ORDER_ID + "} > 0"))
						.setBody(simple("order with id: ${header." + NomNomConstants.HEADER_ORDER_ID + "} received!"))
					.otherwise()
						.setBody(simple("invalid order!"))
				.end();


        /*
        * check if SMS order is of type SMS and contains keyword 'hungry'
        * perform authentication and extract customerId
        * */
        from("direct:postOrderWithSMS")
                .filter(simple("${in.body.type} == 'SMS' && ${in.body.text} contains 'hungry'")) /*IGNORE OTHER Messages */
                    .bean(SMSAuthBean.class)
                    .filter(simple("${in.body.userId.present} == true"))
						.wireTap("metrics:counter:requests:RANDOM?increment=1")
                        .to("direct:enrichCustomerData")
                    .end()
                .end();

        /* extract customerId from header */
        from("direct:postOrderWithREGULAR")
                .bean(RegularAuthBean.class)
                .filter(simple("${in.body.userId.present} == true"))
					.wireTap("metrics:counter:requests:REGULAR?increment=1")
                	.to("direct:enrichCustomerData")
                .end();

        /* enrich order-request with customer data from DB and transform to Order */
        from("direct:enrichCustomerData")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.enrichCustomerData?level=DEBUG")
                .enrich("direct:loadUser", new EnrichCustomer())
                .to("direct:storeOrder").end();
        
		/* load user data from SQL DB */
        from("direct:loadUser")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.loadUser?level=DEBUG")
                .bean(LoadCustomerFromOrderBean.class);

        /* store order in DB */
        from("direct:storeOrder")
                .bean(StoreOrderBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.storeOrder.after?level=DEBUG")
                .choice()
	                .when(header(NomNomConstants.HEADER_TYPE).isEqualTo(OrderType.SMS))
	                	.to("direct:hungryDish")
	                .otherwise()
	                	.to("direct:regularDish")
                .end();

        /* set price limit for menu items ordered by sms */
        from("direct:hungryDish")
                .setBody().simple("{ \"menu.price\": { $gt: 0, $lt: 20 }}").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.hungryDish?level=DEBUG")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findAll")
                .to("direct:splitRestaurants");


        /* get dish names and put them in header */
        from("direct:regularDish")
                .bean(ExtractDishRestaurantBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.regularDish?level=DEBUG")
                .to("direct:findAll");

        /* query restaurants for dishes */
        from("direct:findAll")
                .setBody().simple("{\"menu.name\":{ $in: [${header." + NomNomConstants.HEADER_DISH_NAMES + "}]}}").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.findAll?level=DEBUG")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findAll").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.findAll?level=DEBUG")
                .choice()
                    .when(simple("${in.body.size} > 0"))
                    	.to("direct:splitRestaurants")
                    .otherwise()
                    	.setHeader(NomNomConstants.HEADER_ORDER_STATE, constant(OrderState.REJECTED_NO_RESTAURANTS))
                    	.to("direct:rejectOrder")
                .end();

        /* convert to restaurant data */
        from("direct:splitRestaurants")
        		.split(body())
                .convertBodyTo(String.class)
                .unmarshal(restaurantjsonformat)
                .aggregate(header(NomNomConstants.HEADER_ORDER_ID), new RestaurantDataAggregation()).completionTimeout(NomNomConstants.AGGREGATION_TIMEOUT)
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.splitRestaurants?level=DEBUG")
                .choice()
                    .when(header(NomNomConstants.HEADER_TYPE).isEqualTo(OrderType.SMS))
                    	.to("direct:extractHungryDish")
                    .when(header(NomNomConstants.HEADER_TYPE).isEqualTo(OrderType.REGULAR))
                    	.to("direct:extractRegularDish")
                    .otherwise()
                    	.to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.splitRestaurants_undefinedType?level=ERROR")
                .end();


        /* select a random dish and according restaurant */
        from("direct:extractHungryDish")
                .bean(RandomDishBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.extractHungryDish?level=DEBUG")
                .to("direct:checkRestaurantsAvailability")
                .end();

        /* select restaurants for the regular dishes */
        from("direct:extractRegularDish")
                .enrich("direct:loadOrder", new DishesOrderAggregation())
                .bean(RegularDishQueryRestaurantBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.extractRegularDish?level=DEBUG")
                .to("direct:checkRestaurantsAvailability")
                .end();

        /* load order data from SQL DB */
        from("direct:loadOrder")
		        .bean(LoadOrderBean.class)
		        .end();

        /* if there are restaurants capable of serving the order, continue by checking capacities of the restaurants */
        from("direct:checkRestaurantsAvailability")
        		.choice()
	                .when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.ENRICHED))
	                	.to("direct:checkCapacity")
	                .when((header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_RESTAURANTS)))
	                	.to("direct:rejectOrder")
                .end();

        /* scatter-gather: ask all possible restaurants for their capacity, so given they have capacity, they answer with their ID, else -1 */
        from("direct:checkCapacity")
                .bean(TransformRestaurantHeaderBean.class)
                .setBody(constant(null))
                .recipientList(header(NomNomConstants.HEADER_RESTAURANTS))
                .timeout(NomNomConstants.AGGREGATION_TIMEOUT)
                .parallelProcessing()
                .aggregationStrategy(new CapacityAggregationStrategy()).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.requestCapacity?level=DEBUG")
                .choice()
	                .when(simple("${body.size} > 0"))
	                	.to("direct:selectBestFitRestaurant")
	                .otherwise()
		                .setHeader(NomNomConstants.HEADER_ORDER_STATE).constant(OrderState.REJECTED_NO_CAPACITY)
		                .to("direct:rejectOrder")
	            .end();
        
        /* select restaurant with best price */
        from("direct:selectBestFitRestaurant")
                .split(body())
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findById")
                .convertBodyTo(String.class)
                .unmarshal(restaurantjsonformat)
                .aggregate(header(NomNomConstants.HEADER_ORDER_ID), new RestaurantDataAggregation()).completionTimeout(NomNomConstants.AGGREGATION_TIMEOUT)
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.selectBestFitRestaurant?level=DEBUG")
                .bean(SelectBestFitRestaurantBean.class)
                .to("direct:checkCreditCard")
                .end();

        /* check for valid credit card */
        from("direct:checkCreditCard")
                .bean(PrepareForCreditCheckBean.class)
                .setHeader(Exchange.HTTP_URI, simple("http://localhost:8080/external/creditcards/"
                		+ "${header." + NomNomConstants.HEADER_CREDIT_CARD + "}"
                		+ "?amount=${header." + NomNomConstants.HEADER_AMOUNT + "}"))
                .setHeader(Exchange.CONTENT_TYPE, constant(org.springframework.http.MediaType.APPLICATION_JSON_VALUE))
                .setBody(constant(null))
                .to("http://dummyHost")
                .unmarshal().json(JsonLibrary.Jackson, PaymentRequestAnswer.class)
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.checkCreditCard?level=DEBUG")
                .choice()
	                .when(simple("${in.body.liquid} == true"))
	                	.to("direct:sendOrderToRestaurant")
	                .otherwise()
	                	.setHeader(NomNomConstants.HEADER_ORDER_STATE).constant(OrderState.REJECTED_INVALID_PAYMENT)
	                	.to("direct:rejectOrder")
                .end();

        /* sending order to restaurant through wiretap */
        from("direct:sendOrderToRestaurant")
                .bean(LoadOrderBean.class)
                .wireTap("direct:updateOrder")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.sendOrderToRestaurant?level=DEBUG")
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_URI, simple("http://localhost:8080/external/restaurants/${header." + NomNomConstants.HEADER_RESTAURANT_ID + "}/order"))
                .setHeader(Exchange.CONTENT_TYPE, constant(org.springframework.http.MediaType.APPLICATION_JSON_VALUE))
                .to("http://dummyHost")                       
                .end();

        /* update the order, update DB */
        from("direct:updateOrder")
                .bean(LoadOrderBean.class)
                .setHeader(NomNomConstants.HEADER_ORDER_STATE, constant(OrderState.FULLFILLED))
                .bean(UpdateOrderBean.class)
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.updateOrder?level=DEBUG")
                .to("direct:finishOrder");

        /*  finish order */
        from("direct:finishOrder")
				.wireTap("metrics:counter:orders:FINISHED?increment=1")
                .to("direct:notifyCustomer");

        /*reject order, update DB*/
        from("direct:rejectOrder")
                .bean(LoadOrderBean.class)
                .bean(UpdateOrderBean.class)
				.wireTap("metrics:counter:orders:REJECTED?increment=1")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.rejectOrder?level=DEBUG")
                .to("direct:notifyCustomer");

        /*notify customer via channel*/
        from("direct:notifyCustomer")
				.throttle(simple("{{notify.maximumRequestsPerPeriod}}"))
        		.timePeriodMillis(NomNomConstants.THROTTLER_PERIOD)
        		//does not work: .timePeriodMillis(Long.perseLong(SpringPropterty)).timePeriodMillis(simple("{{notify.timePeriodMillis}}", Long.class))
                .to("direct:sendCustomerNotification");

    }

}