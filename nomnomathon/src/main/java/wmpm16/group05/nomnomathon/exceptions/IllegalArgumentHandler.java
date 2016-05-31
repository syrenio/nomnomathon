package wmpm16.group05.nomnomathon.exceptions;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IllegalArgumentHandler {

    public void handle(Exchange exchange){
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);

        IllegalArgumentException ex = exchange.getProperty(ExceptionUtil.CAMEL_EXCEPTION_CAUGHT,IllegalArgumentException.class);

        Map<String,String> map = new HashMap<String,String>();
        String text = String.format("The commited data format is invalid: '%s'", ex.getMessage());
        map.put("error", text);
        exchange.getOut().setBody(map);
    }
}