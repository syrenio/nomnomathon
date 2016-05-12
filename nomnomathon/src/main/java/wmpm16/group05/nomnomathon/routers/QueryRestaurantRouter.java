package wmpm16.group05.nomnomathon.routers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import wmpm16.group05.nomnomathon.processor.QueryRestaurantProcessor;

/**
 * Created by Agnes on 12.05.16.
 */
@Component
public class QueryRestaurantRouter extends RouteBuilder {



    @Override
    public void configure() throws Exception {


        from("direct:findAll")
                .to("mongodb:mongoDb?database=restaurant_data&collection=restaurant_data" +
                        "&operation=findAll").process(new QueryRestaurantProcessor())
                .to("mock:resultFindAll");
    }
}
