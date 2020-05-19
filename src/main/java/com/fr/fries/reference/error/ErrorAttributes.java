package com.fr.fries.reference.error;

import com.fr.fries.reference.common.Constants;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

        return reformatErrorAttributes(errorAttributes);
    }

    private Map<String, Object> reformatErrorAttributes(Map<String, Object> webAttributes) {
        String status = webAttributes.get(Constants.ATTR_STATUS).toString();
        String error = webAttributes.get(Constants.ATTR_ERROR).toString();
        String message = webAttributes.get(Constants.ATTR_MESSAGE).toString();

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(Constants.ATTR_RETURN_CODE, Long.parseLong(Constants.ATTR_RCODE_PREFIX + status));
        if (message.equals(Constants.ATTR_NO_MESSAGE_AVAILABLE)) {
            errorAttributes.put(Constants.ATTR_RETURN_MESSAGE, error);
        } else {
            errorAttributes.put(Constants.ATTR_RETURN_MESSAGE, error + ": " + message);
        }

        return errorAttributes;
    }
}
