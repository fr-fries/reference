package com.fr.fries.reference.error;

import com.fr.fries.reference.common.ResponseCode;
import lombok.Getter;

@Getter
public class SignUrlException extends Exception {

    private final ResponseCode responseCode;

    public SignUrlException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public SignUrlException(ResponseCode responseCode, Exception exception) {
        super(responseCode.getMessage(), exception);
        this.responseCode = responseCode;
    }

    public SignUrlException(ResponseCode responseCode, String message) {
        super(String.format(responseCode.getMessage(), message));
        this.responseCode = responseCode;
    }
}
