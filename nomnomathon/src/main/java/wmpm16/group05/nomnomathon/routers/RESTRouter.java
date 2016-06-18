package wmpm16.group05.nomnomathon.routers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.aggregation.*;
import wmpm16.group05.nomnomathon.beans.*;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;
import wmpm16.group05.nomnomathon.domain.RestaurantData;
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
                .enrich("direct:loadUser", new EnrichCustomer())
                .to("direct:storeOrder").end();
        
		/*load user data from SQL DB*/
        from("direct:loadUser")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.loadUser?level=DEBUG")
                .bean(LoadCustomerFromOrderBean.class);

        /*store order in DB*/
        from("direct:storeOrder")
                .bean(StoreOrderBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.storeOrder.after?level=DEBUG")
                .choice()
	                .when(header(NomNomConstants.HEADER_TYPE).isEqualTo(OrderType.SMS))
	                	.to("direct:hungryDish")
	                .otherwise()
	                	.to("direct:regularDish")
                .end();

        from("direct:hungryDish")
                .setBody().simple("{ \"menu.price\": { $gt: 0, $lt: 20 }}").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.hungryDish?level=DEBUG")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findAll")
                .to("direct:splitRestaurants");


        /*query restaurants for dishes*/
        from("direct:regularDish")
                .bean(ExtractDishRestaurantBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.regularDish?level=DEBUG")
                .to("direct:findAll");

        from("direct:findAll")
                .setBody().simple("{\"menu.name\":{ $in: [${header." + NomNomConstants.HEADER_DISH_NAMES + "}]}}").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.findAll?level=DEBUG")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data&operation=findAll").to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.findAll?level=DEBUG")
                .choice()
                    .when(simple("${in.body.size} > 0"))
                    	.to("direct:splitRestaurants")
                    .when(simple("${in.body.size} == 0"))
                    	.setHeader(NomNomConstants.HEADER_ORDER_STATE, constant(OrderState.REJECTED_NO_RESTAURANTS))
                    	.to("direct:rejectOrder")
                .end()
                .end();
                //.to("direct:splitRestaurants");


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
                .end()
                .end();


        from("direct:extractHungryDish")
                .bean(RandomDishBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.extractHungryDish?level=DEBUG")
                .to("direct:checkRestaurantsAvailability")
                .end();

        from("direct:extractRegularDish")
                .enrich("direct:loadOrder", new DishesOrderAggregation())
                .bean(RegularDishQueryRestaurantBean.class).to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.extractRegularDish?level=DEBUG")
                .to("direct:checkRestaurantsAvailability")
                .end();


        /*load order data from SQL DB*/
        from("direct:loadOrder")
		        .bean(LoadOrderBean.class)
		        .end();


        from("direct:checkRestaurantsAvailability")
                .choice()
	                .when(header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.ENRICHED))
	                	.to("direct:requestCapacity")
	                .when((header(NomNomConstants.HEADER_ORDER_STATE).isEqualTo(OrderState.REJECTED_NO_RESTAURANTS)))
	                	.to("direct:rejectOrder")
                .end();

        /**
         *  Maintainer: till
         *  Scatter-Gather: Ask all possible restaurants for their capacity.
         *  Given they have capacity, they answer with their ID, else -1.
         */
        from("direct:requestCapacity")
                .bean(TransformRestaurantHeaderBean.class)
                .setBody(constant(null))
                .recipientList(header(NomNomConstants.HEADER_RESTAURANTS))
                .parallelProcessing()
                .aggregationStrategy(new CapacityAggregationStrategy())
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.requestCapacity?level=DEBUG")
                .to("direct:checkRestaurantAvailable");

        /**
         * Maintainer: till
         * Message Filter: Drop all non valid Restaurant IDs here.
         */
        from("direct:checkRestaurantAvailable")
                .split(body())
                .filter(body().isNotEqualTo("-1"))
	                .aggregate(header(NomNomConstants.HEADER_ORDER_ID), new CapacityAggregationStrategy()).completionTimeout(NomNomConstants.AGGREGATION_TIMEOUT)
	                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.checkRestaurantAvailable?level=DEBUG")
	                .choice()
		                .when(simple("${body.size} > 0"))
		                	.to("direct:selectBestFitRestaurant")
		                .otherwise()
			                .setHeader(NomNomConstants.HEADER_ORDER_STATE).constant(OrderState.REJECTED_NO_CAPACITY)
			                .to("direct:rejectOrder")
	                .end();


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

        /**
         *  Maintainer: till
         *  Wiretap: Sending order to restaurant.
         */
        from("direct:sendOrderToRestaurant")
                .bean(LoadOrderBean.class)
                .wireTap("direct:updateOrder")
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.sendOrderToRestaurant?level=DEBUG")
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_URI, simple("http://localhost:8080/external/restaurants/${header." + NomNomConstants.HEADER_RESTAURANT_ID + "}/order"))
                .setHeader(Exchange.CONTENT_TYPE, constant(org.springframework.http.MediaType.APPLICATION_JSON_VALUE))
                .to("http://dummyHost")                       
                .end();

        from("direct:updateOrder")
                .bean(LoadOrderBean.class)
                .setHeader(NomNomConstants.HEADER_ORDER_STATE, constant(OrderState.FULLFILLED))
                .bean(UpdateOrderBean.class)
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.updateOrder?level=DEBUG")
                .to("direct:finishOrder");

        from("direct:finishOrder")
                .to("direct:notifyCustomer");

        /*reject order, update DB*/
        from("direct:rejectOrder")
                .bean(LoadOrderBean.class)
                .bean(UpdateOrderBean.class)
                .to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.rejectOrder?level=DEBUG")
                .to("direct:notifyCustomer");

        /*notify customer via channel*/
        from("direct:notifyCustomer")
				.throttle(simple("{{notify.maximumRequestsPerPeriod}}"))
        		.timePeriodMillis(NomNomConstants.THROTTLER_PERIOD)
        		//does not work: .timePeriodMillis(Long.perseLong(SpringPropterty)).timePeriodMillis(simple("{{notify.timePeriodMillis}}", Long.class))
                .to("direct:sendCustomerNotification");

        /* Next processes*/



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