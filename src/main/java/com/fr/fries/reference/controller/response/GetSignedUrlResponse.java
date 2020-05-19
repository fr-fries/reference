package com.fr.fries.reference.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fr.fries.reference.sign.object.SignedUrl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetSignedUrlResponse extends ServletResponse {

    String objectId;
    long ttl;
    SignedUrl uploadInfo;
    SignedUrl downloadInfo;

    public GetSignedUrlResponse(String objectId, SignedUrl signedUrl, long ttl) {
        this.objectId = objectId;
        this.uploadInfo = signedUrl;
        this.ttl = ttl;
    }

    public GetSignedUrlResponse(String objectId, SignedUrl signedUrl) {
        this.objectId = objectId;
        this.downloadInfo = signedUrl;
    }
}
