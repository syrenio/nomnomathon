package wmpm16.group05.nomnomathon.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by syrenio on 5/4/2016.
 */
@RestController
public class DemoController {
    @Autowired
    private CamelContext context;

    @RequestMapping
    private void getStart() throws Exception {
        System.out.println(context);
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:start",null);
    }
}
