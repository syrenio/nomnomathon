package wmpm16.group05.nomnomathon.exceptions;

import org.apache.camel.Exchange;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BeanValidationHandler {

    public void handle(Exchange exchange){
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);

        BeanValidationException ex = exchange.getProperty(ExceptionUtil.CAMEL_EXCEPTION_CAUGHT,BeanValidationException.class);

        Map<String,String> map = new HashMap<String,String>();
        String text = String.format("The commited data is invalid: '%s'", ex.getMessage());
        map.put("error", text);
        exchange.getOut().setBody(map);
    }
}