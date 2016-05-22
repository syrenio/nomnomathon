package wmpm16.group05.nomnomathon.exceptions;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by syrenio on 22/05/16.
 */
@Component
public class UnrecognizedPropertyHandler {

    public void handle(Exchange exchange){
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);

        UnrecognizedPropertyException ex = exchange.getProperty(ExceptionUtil.CAMEL_EXCEPTION_CAUGHT,UnrecognizedPropertyException.class);

        Map<String,String> map = new HashMap<String,String>();
        String text = String.format("Invalid request payload format. Unknown property: '%s'", ex.getPropertyName());
        map.put("error", text);
        exchange.getOut().setBody(map);
    }
}
