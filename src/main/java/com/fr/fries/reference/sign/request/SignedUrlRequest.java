package com.fr.fries.reference.sign.request;

import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fr.fries.reference.common.SignerType;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import com.fr.fries.reference.utils.KeyUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignedUrlRequest {

    String userId;
    String cid;
    String objectId;
    long reqTime;

    HttpMethod method;
    SignerType type;
    String domain;
    long expireTime;
    String key;
    Duration ttl;

    String contentType;
    Long contentLength;

    String resFilename;
    String resContentType;

    public SignedUrlRequest(String userId, String cid, String contentType, Long contentLength) {
        this.unitId = unitId;
        this.cid = cid;
        this.objectId = KeyUtils.generateObjectId();
        this.reqTime = System.currentTimeMillis();

        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public SignedUrlRequest(String userId, String cid, String objectId, String resFilename, String resContentType) {
        this.unitId = unitId;
        this.cid = cid;
        this.objectId = objectId;
        this.reqTime = System.currentTimeMillis();

        this.resFilename = resFilename;
        this.resContentType = resContentType;
    }

    public void setUrlPolicy(UrlPolicy urlPolicy) throws SignUrlException {
        this.method = urlPolicy.getMethod();
        this.type = urlPolicy.getType();
        this.domain = urlPolicy.getDomain();
        this.expireTime = urlPolicy.getExpiresMillis(reqTime);
        this.key = KeyUtils.getS3Key(this.cid, this.userId, this.objectId);
    }
}
