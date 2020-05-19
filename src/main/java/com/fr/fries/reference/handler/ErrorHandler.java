package com.fr.fries.reference.handler;

import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.controller.response.ServletResponse;
import com.fr.fries.reference.error.SignUrlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static com.fr.fries.reference.common.Constants.*;
import static com.fr.fries.reference.common.ResponseCode.BAD_REQUEST;
import static com.fr.fries.reference.common.ResponseCode.UNEXPECTED;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private static final int STACK_TRACE_SIZE = 5;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    @NonNull
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
                                                          HttpStatus status, @NonNull WebRequest request) {
        long rcode = Long.parseLong(ATTR_RCODE_PREFIX + status.value());
        String rmsg = makeErrorMessage(status.getReasonPhrase(), ex.getMessage());

        request.setAttribute(ATTR_RETURN_CODE, rcode, SCOPE_REQUEST);
        request.setAttribute(ATTR_RETURN_MESSAGE, rmsg, SCOPE_REQUEST);

        log.error(getErrorLogParams(rcode, rmsg, ex, false));

        return new ResponseEntity<>(new ServletResponse(rcode, rmsg), headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServletResponse> handleException(Exception ex, HttpServletRequest request) {
        return makeErrorResponseView(request, UNEXPECTED, ex, null, true);
    }

    @ExceptionHandler(value = {MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    public ResponseEntity<ServletResponse> handleMissingOrMismatchException(Exception ex, HttpServletRequest request) {
        return makeErrorResponseView(request, BAD_REQUEST, ex, null, false);
    }

    @ExceptionHandler(SignUrlException.class)
    public ResponseEntity<ServletResponse> handleSignUrlException(SignUrlException signUrlEx, HttpServletRequest request) {
        return makeErrorResponseView(request, signUrlEx.getResponseCode(), signUrlEx, signUrlEx.getMessage(), false);
    }

    private ResponseEntity<ServletResponse> makeErrorResponseView(HttpServletRequest request, ResponseCode returnCode,
                                                                  Exception ex, String errorMessage, boolean isFullStack) {
        long rcode = returnCode.getCode();
        String rmsg = errorMessage != null
                ? errorMessage : makeErrorMessage(returnCode.getMessage(), ex.getMessage());

        request.setAttribute(ATTR_RETURN_CODE, rcode);
        request.setAttribute(ATTR_RETURN_MESSAGE, rmsg);

        log.error(getErrorLogParams(rcode, rmsg, ex, isFullStack));

        return new ResponseEntity<>(new ServletResponse(rcode, rmsg), returnCode.getHttpStatus());
    }

    private String makeErrorMessage(String description, String message) {
        if (message == null)
            return description;

        List<String> errorMessageList = new ArrayList<>();
        for (String msg1 : message.split(", ")) {
            for (String msg2 : msg1.split("; ")) {
                if (msg2.contains(": "))
                    msg2 = msg2.substring(msg2.indexOf(':') + 2);

                errorMessageList.add(msg2);
            }
        }
        return description + ": " + StringUtils.join(errorMessageList, ", ");
    }

    private String getErrorLogParams(long rcode, String rmsg, Exception ex, boolean isFullStack) {
        String stackTrace = isFullStack ? ExceptionUtils.getStackTrace(ex) : getShortStackTrace(ex);
        return String.format("rcode: {}, rmsg: {}, stackTrace: {}", rcode, rmsg, stackTrace);
    }

    private String getShortStackTrace(Exception exception) {
        return StringUtils.join(ExceptionUtils.getStackFrames(exception), "\n", 0, STACK_TRACE_SIZE);
    }
}
