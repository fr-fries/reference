package com.fr.fries.reference.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(HttpStatus.OK, 0, "Success"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 114400, "Bad Request"),
    RESPONSE_FILENAME_REQUIRED(HttpStatus.BAD_REQUEST, 114400,
            "Bad Request: Missing request header 'x-su-response-filename' for method parameter of type String"),
    NOT_FOUND(HttpStatus.NOT_FOUND, 114404, "Not Found"),
    OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, 114404, "Object does not exist"),
    REQUEST_CONTENT_LENGTH_TOO_LONG(HttpStatus.PAYLOAD_TOO_LARGE, 114413, "Request header 'x-su-content-length' " +
            "is too long. It must not exceed the maximum assigned value, %s"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 114500, "Internal Server Error"),

    UNEXPECTED(HttpStatus.INTERNAL_SERVER_ERROR, 114900, "Unexpected error"),
    INTERNAL_S3_SIGNER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 114901, "S3 signer error"),
    INTERNAL_CF_SIGNER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 114902, "Cloudfront signer error"),
    INTERNAL_KMS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 114903, "KMS key error"),
    INTERNAL_MESSAGE_DIGEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 114904, "Message digest error"),
    INTERNAL_POLICY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 114905, "Policy error"),
    INTERNAL_S3_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 114906, "S3 client error");

    private final HttpStatus httpStatus;
    private final long code;
    private String message;
}
