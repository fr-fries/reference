package com.fr.fries.reference.handler;

import com.fr.fries.reference.common.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fr.fries.reference.common.Constants.*;

@Component
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "handler")
public class LogInterceptor extends HandlerInterceptorAdapter {

    private List<String> accessLogAttributes;
    private List<String> encryptedAccessLogAttributes;
    private Map<String, Object> logAttributes = new HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(ATTR_RETURN_CODE, ResponseCode.SUCCESS.getCode());
        request.setAttribute(ATTR_RETURN_MESSAGE, ResponseCode.SUCCESS.getMessage());

        if (request.getHeader(HEADER_APP_ID) != null)
            request.setAttribute(ATTR_APP_ID, request.getHeader(HEADER_APP_ID));

        if (request.getHeader(HEADER_USER_ID) != null)
            request.setAttribute(ATTR_USER_ID, request.getHeader(HEADER_USER_ID));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (request.getAttribute(ATTR_API_NAME) == null)
            request.setAttribute(ATTR_API_NAME, getApiName(request.getRequestURI()));

        writeAccessLogAttributes(request);
        writeEncryptedAccessLogAttributes(request);
    }

    private String getApiName(String uri) {
        return uri.startsWith(URI_ADMIN_API) ? API_ADMIN : API_NOT_IMPLEMENT;
    }

    private void writeAccessLogAttributes(HttpServletRequest request) {
        for (String name : accessLogAttributes) {
            if (request.getAttribute(name) != null)
                logAttributes.put(name, request.getAttribute(name));
        }
    }

    private void writeEncryptedAccessLogAttributes(HttpServletRequest request) {
        for (String name : encryptedAccessLogAttributes) {
            if (request.getAttribute(name) != null)
                logAttributes.put(name, request.getAttribute(name));
        }
    }
}
