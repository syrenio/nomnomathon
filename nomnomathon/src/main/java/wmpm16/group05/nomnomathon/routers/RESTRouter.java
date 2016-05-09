package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;

/**
 * Created by syrenio on 5/3/2016.
 */
@Component
public class RESTRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        rest("/")
                .get("/status").to("direct:status")
                .post("/orders").type(OrderRequest.class).to("direct:postOrder")
                .get("/restaurants/{id}").to("direct:callRestaurant");
        from("direct:status")
                .transform().constant("running!");
        from("direct:callRestaurant")
                .setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
                .to("http://petstore.swagger.io/v2/store/inventory");

        from("direct:postOrder")
                .choice()
                .when(exchange -> exchange.getIn().getBody(OrderRequest.class).getType() == OrderType.SMS)
                .to("direct:postOrderWithSMS")
                .when(exchange -> exchange.getIn().getBody(OrderRequest.class).getType() == OrderType.REGULAR)
                .to("direct:postOrderWithREGULAR")
                .end()
                .process(x -> {
                    System.out.println(x.getIn());
                });

        from("direct:postOrderWithSMS")
                .to("direct:checkUserToken");

        from("direct:postOrderWithREGULAR")
                .to("direct:checkUserToken");

        from("direct:checkUserToken")
                .to("direct:enrichCustomerData");

        from("direct:enrichCustomerData")
                .to("direct:storeOrder");

        from("direct:storeOrder")
                .to("direct:queryRestaurants");

        from("direct:queryRestaurants")
                /*choice or something here,  reject or next step in process*/
                .to("direct:rejectOrder");

        from("direct:rejectOrder")
                .to("direct:notifyCustomer");

        /* possible process nodes */

        //from("direct:checkUserToken")
        //from("direct:enrichCustomerData")
        //from("direct:storeOrder")
        //from("direct:queryRestaurants")
        //from("direct:rejectOrder") /* update order in database*/

        //from("direct:notifyCustomer")

        /**/

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
