package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.beans.QueryRestaurantBean;

/**
 * Created by Agnes on 12.05.16.
 */
@Component
public class QueryRestaurantRouter extends RouteBuilder {



    @Override
    public void configure() throws Exception {


        from("direct:findAll1")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data" +
                        "&operation=findAll")
                .bean(QueryRestaurantBean.class)
                .to("mock:resultFindAll");
    }
}
