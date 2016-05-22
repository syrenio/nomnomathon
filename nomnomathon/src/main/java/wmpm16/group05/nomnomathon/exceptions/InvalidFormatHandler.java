package wmpm16.group05.nomnomathon.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.aspectj.bridge.Version.text;

/**
 * Created by syrenio on 22/05/16.
 */
@Component
public class InvalidFormatHandler {

    public void handle(Exchange exchange){

        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);

        InvalidFormatException ex = exchange.getProperty(ExceptionUtil.CAMEL_EXCEPTION_CAUGHT,InvalidFormatException.class);

        Map<String,String> map = new HashMap<>();
        String text = "";

        if(ex.getTargetType().isEnum()){
            List<?> values = Arrays.asList(ex.getTargetType().getEnumConstants());
            text = String.format("Unknown value: '%s'. Valid values: %s", ex.getValue(), values);
        }else{
            text = "???";
        }
        map.put("error", text);
        exchange.getOut().setBody(map);
    }
}
