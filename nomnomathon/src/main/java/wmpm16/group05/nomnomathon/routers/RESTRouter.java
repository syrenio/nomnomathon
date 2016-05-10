package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.domain.OrderRequest;
import wmpm16.group05.nomnomathon.domain.OrderType;
import wmpm16.group05.nomnomathon.domain.RestaurantCapacityResponse;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by syrenio on 5/3/2016.
 */
@Component
public class RESTRouter extends RouteBuilder {

    private static final String HEADER_Authorization = "Authorization";

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
                .process(x -> {
                    System.out.println("SMS " + x.getIn());
                })
                .to("direct:checkUserToken");

        from("direct:postOrderWithREGULAR")
                .process(req -> {
                })
                .to("direct:checkUserToken");

        from("direct:checkUserToken")
                .process(req -> {

                    /*extract userId from header: Authorization  --> Basic Base64(username:password)*/
                    /*maybe use X-Auth-Token or Authorization*/

                    String auth_header = (String) req.getIn().getHeader(HEADER_Authorization);
                    String username = null;
                    String password = null;

                    if (auth_header != null && auth_header.startsWith("Basic")) {
                        final String[] values = extractBasicAuth(auth_header);
                        username = values[0];
                        password = values[1];
                    }

                    /*TODO check in DB*/
                    /*TODO insert UserID into order-request*/

                    OrderRequest body = req.getIn().getBody(OrderRequest.class);
                    if(username!= null && password != null){
                        if(username.equals("bernd") && password.equals("nomnom")){
                            body.setUserId(Optional.of(159l));
                        }
                    }
                    req.getIn().setBody(body);
                    System.out.println(body.getUserId().get());
                })
                //.to("sql:select order_seq.nextval from dual?outputHeader=OrderId&outputType=SelectOne")
                /*choice customer exists and is valid*/
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

    private String[] extractBasicAuth(String auth_header) {
        // Authorization: Basic base64credentials
        String base64Credentials = auth_header.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                Charset.forName("UTF-8"));
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        System.out.println("auth: " + values[0] + ":"+ values[1]);
        return values;
    }

}
