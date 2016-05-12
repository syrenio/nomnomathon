package wmpm16.group05.nomnomathon;

import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * Created by Agnes on 12.05.16.
 */

public class IsMockEndpoints extends CamelTestSupport {

    @Override
    public String isMockEndpoints() {
        // override this method and return the pattern for which endpoints to mock.
        // use * to indicate all
        return "*";
    }

    @Test
    public void testMockAllEndpoints() throws Exception {


        // notice we have automatic mocked all endpoints and the name of the endpoints is "mock:uri"
        getMockEndpoint("mock:resultFindAll").expectedBodiesReceived("Hello World");

        template.sendBody("direct:findAll", null);

//        assertMockEndpointsSatisfied();

        // additional test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:findAll"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:findAll").to("mock:resultfindAll");
            }
        };
    }
}
