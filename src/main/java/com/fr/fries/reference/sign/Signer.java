package com.fr.fries.reference.sign;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.config.PolicyConfig;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import com.fr.fries.reference.sign.object.PresignedUrl;
import com.fr.fries.reference.sign.object.SignedUrl;
import com.fr.fries.reference.sign.request.SignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public abstract class Signer {

    final PolicyConfig policyConfig;
    final AmazonS3 s3Client;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public abstract PresignedUrl generateSignedUrl(UrlPolicy urlPolicy, String objectKey, Date reqDate)
            throws SignUrlException;

    public abstract SignedUrl generateSignedUrl(SignedUrlRequest req) throws SignUrlException;

    public void validatePutSignedUrlRequest(SignedUrlRequest req) throws SignUrlException {
        DataSize maxContentLength = policyConfig.getMaxContentLength(req.getCid());
        if (maxContentLength != null && req.getContentLength() > maxContentLength.toBytes())
            throw new SignUrlException(ResponseCode.REQUEST_CONTENT_LENGTH_TOO_LONG, maxContentLength.toString());
    }

    public void validateGetSignedUrlRequest(SignedUrlRequest req) throws SignUrlException {
        if (policyConfig.isResFilenameRequired(req.getCid()) && req.getResFilename() == null)
            throw new SignUrlException(ResponseCode.RESPONSE_FILENAME_REQUIRED);

        String originBucket = policyConfig.getOriginBucket(req.getCid());
        List<S3ObjectSummary> s3ObjectSummaries = s3Client.listObjects(originBucket, req.getKey()).getObjectSummaries();
        log.debug("[Signer] S3 key: {}, resources: {}", req.getKey(), s3ObjectSummaries.size());
        if (s3ObjectSummaries.isEmpty())
            throw new SignUrlException(ResponseCode.OBJECT_NOT_FOUND);
    }
}
