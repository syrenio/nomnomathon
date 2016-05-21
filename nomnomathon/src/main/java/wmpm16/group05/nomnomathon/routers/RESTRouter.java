package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import wmpm16.group05.nomnomathon.aggregation.EnrichCustomer;
import wmpm16.group05.nomnomathon.beans.PollCustomerFromOrder;
import wmpm16.group05.nomnomathon.beans.StoreOrderBean;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;
import wmpm16.group05.nomnomathon.processor.AuthProcessor;

/**
 * Created by syrenio on 5/3/2016.
 */
@Component
public class RESTRouter extends RouteBuilder {

    @Autowired
    private AuthProcessor authProcessor;
    
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
                .end();

        from("direct:postOrderWithSMS")
                .filter(simple("${in.body.type} == 'SMS' && ${in.body.text} contains 'hungry'")) /*IGNORE OTHER Messages */
                //.to("jpa://wmpm16.group05.nomnomathon.models.Customer?consumer.query=select o from wmpm16.group05.nomnomathon.models.Customer o where o.id = 1")
                .to("direct:checkUserToken")
                .end();

        from("direct:postOrderWithREGULAR")
                .process(req -> {
                })
                .to("direct:checkUserToken");

        from("direct:checkUserToken")
                .process(authProcessor)
                //.to("sql:select order_seq.nextval from dual?outputHeader=OrderId&outputType=SelectOne")
                /*choice customer exists and is valid*/
                .to("direct:enrichCustomerData");

        from("direct:enrichCustomerData")
        		.to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.enrichCustomerData?level=DEBUG")
        		.enrich("direct:pollUser", new EnrichCustomer())
                .to("direct:storeOrder").end();
        
        from("direct:pollUser")
        		.to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.pollUser?level=DEBUG")
        		.bean(PollCustomerFromOrder.class);

        from("direct:storeOrder")
        		.to("log:wmpm16.group05.nomnomathon.routers.RESTRouter.storeOrder?level=DEBUG")
        		.bean(StoreOrderBean.class)
                .to("direct:queryRestaurants");

        from("direct:queryRestaurants")
                /*choice or something here,  reject or next step in process*/
                .to("direct:rejectOrder");

        from("direct:rejectOrder")
                .to("direct:notifyCustomer");

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
