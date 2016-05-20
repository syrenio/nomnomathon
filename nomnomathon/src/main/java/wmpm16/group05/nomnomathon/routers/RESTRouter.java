package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.beans.RegularAuthBean;
import wmpm16.group05.nomnomathon.beans.SMSAuthBean;
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
                .to("direct:enrichCustomerData")
                .end();

        /*extract customerId from header */
        from("direct:postOrderWithREGULAR")
                .bean(RegularAuthBean.class)
                .to("direct:enrichCustomerData");

        /*enrich order-request with customer data from DB and transform to Order*/
        from("direct:enrichCustomerData")
                .to("direct:storeOrder");

        /*store order in DB*/
        from("direct:storeOrder")
                .to("direct:queryRestaurants");

        /*query restaurants for dishes*/
        from("direct:queryRestaurants")
                /*choice or something here,  reject or next step in process*/
                .to("direct:rejectOrder");

        /*reject order, update DB*/
        from("direct:rejectOrder")
                .to("direct:notifyCustomer");

        /*notify customer via channel*/
        from("direct:notifyCustomer")
                .process(x -> {
                    System.out.println(x.getIn());
                });

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
