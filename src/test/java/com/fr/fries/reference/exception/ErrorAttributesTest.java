package com.fr.fries.reference.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.fries.reference.common.Constants;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.error.ErrorAttributes;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class ErrorAttributesTest {

    @Test
    public void reformatErrorAttributes_whenMessageExists_thenMerged() throws Exception {
        ErrorAttributes signUrlErrorAttributes = new ErrorAttributes();
        Method method = signUrlErrorAttributes.getClass().getDeclaredMethod("reformatErrorAttributes", Map.class);
        method.setAccessible(true);

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("not necessary", "this attribute will be clear");
        errorAttributes.put(Constants.ATTR_STATUS, HttpStatus.SC_BAD_REQUEST);
        errorAttributes.put(Constants.ATTR_ERROR, ResponseCode.BAD_REQUEST.getMessage());
        errorAttributes.put(Constants.ATTR_MESSAGE, "this message will be merged");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> result = objectMapper.convertValue(
                method.invoke(signUrlErrorAttributes, errorAttributes), new TypeReference<Map<String, Object>>() {
                });

        assertThat(result.get("not necessary"), nullValue());
        assertThat(result.get(Constants.ATTR_STATUS), nullValue());
        assertThat(result.get(Constants.ATTR_ERROR), nullValue());
        assertThat(result.get(Constants.ATTR_MESSAGE), nullValue());
        assertThat(result.get(Constants.ATTR_RETURN_CODE), equalTo(ResponseCode.BAD_REQUEST.getCode()));
        assertThat(result.get(Constants.ATTR_RETURN_MESSAGE),
                equalTo(ResponseCode.BAD_REQUEST.getMessage() + ": this message will be merged"));
    }

    @Test
    public void reformatErrorAttributes_whenNoMessage_thenNotMerge() throws Exception {
        ErrorAttributes signUrlErrorAttributes = new ErrorAttributes();
        Method method = signUrlErrorAttributes.getClass().getDeclaredMethod("reformatErrorAttributes", Map.class);
        method.setAccessible(true);

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("not necessary", "this attribute will be clear");
        errorAttributes.put(Constants.ATTR_STATUS, HttpStatus.SC_BAD_REQUEST);
        errorAttributes.put(Constants.ATTR_ERROR, ResponseCode.BAD_REQUEST.getMessage());
        errorAttributes.put(Constants.ATTR_MESSAGE, "No message available");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> result = objectMapper.convertValue(
                method.invoke(signUrlErrorAttributes, errorAttributes), new TypeReference<Map<String, Object>>() {
                });

        assertThat(result.get("not necessary"), nullValue());
        assertThat(result.get(Constants.ATTR_STATUS), nullValue());
        assertThat(result.get(Constants.ATTR_ERROR), nullValue());
        assertThat(result.get(Constants.ATTR_MESSAGE), nullValue());
        assertThat(result.get(Constants.ATTR_RETURN_CODE), equalTo(ResponseCode.BAD_REQUEST.getCode()));
        assertThat(result.get(Constants.ATTR_RETURN_MESSAGE), equalTo(ResponseCode.BAD_REQUEST.getMessage()));
    }
}
