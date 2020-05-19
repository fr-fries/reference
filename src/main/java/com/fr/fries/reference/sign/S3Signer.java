package com.fr.fries.reference.sign;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fr.fries.reference.common.Constants;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.config.PolicyConfig;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import com.fr.fries.reference.sign.object.PresignedUrl;
import com.fr.fries.reference.sign.object.SignedUrl;
import com.fr.fries.reference.sign.request.SignedUrlRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;

@Component
public class S3Signer extends Signer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public S3Signer(PolicyConfig policyConfig, AmazonS3 s3Client) {
        super(policyConfig, s3Client);
    }

    public PresignedUrl generateSignedUrl(UrlPolicy urlPolicy, String objectKey, Date reqDate)
            throws SignUrlException {
        try {
            Date expiration = new Date(urlPolicy.getExpiresMillis(reqDate.getTime()));
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(urlPolicy.getDomain(), objectKey)
                    .withMethod(urlPolicy.getMethod())
                    .withExpiration(expiration);
            URL signedUrl = s3Client.generatePresignedUrl(request);

            return new PresignedUrl(urlPolicy.getMethod().name(), signedUrl.toString(), expiration.getTime());
        } catch (Exception e) {
            log.error("[S3Signer] generate s3 signed url error. {} {}", urlPolicy.getDomain(), objectKey);
            throw new SignUrlException(ResponseCode.INTERNAL_S3_SIGNER_ERROR, e);
        }
    }

    public SignedUrl generateSignedUrl(SignedUrlRequest req) throws SignUrlException {
        try {
            GeneratePresignedUrlRequest s3UrlRequest = makeGeneratePresignedUrlRequest(req);
            URL signedUrl = s3Client.generatePresignedUrl(s3UrlRequest);

            return new SignedUrl(signedUrl.toString(), req.getExpireTime());
        } catch (Exception e) {
            log.error("[S3Signer] generate s3 signed url error. {}, {}", req.toString(), e.getMessage());
            throw new SignUrlException(ResponseCode.INTERNAL_S3_SIGNER_ERROR, e);
        }
    }

    private GeneratePresignedUrlRequest makeGeneratePresignedUrlRequest(SignedUrlRequest req) {
        GeneratePresignedUrlRequest s3UrlRequest = new GeneratePresignedUrlRequest(req.getDomain(), req.getKey())
                .withMethod(req.getMethod())
                .withExpiration(new Date(req.getExpireTime()));

        if (StringUtils.isNotBlank(req.getContentType()))
            s3UrlRequest.setContentType(req.getContentType());

        if (req.getContentLength() != null)
            s3UrlRequest.putCustomRequestHeader(Constants.HEADER_CONTENT_LENGTH, req.getContentLength().toString());

        if (StringUtils.isNotBlank(req.getResFilename()))
            s3UrlRequest.addRequestParameter(Constants.PARAM_RES_CONTENT_DISPOSITION, Constants.ATTR_ATTACHMENT + req.getResFilename());

        if (StringUtils.isNotBlank(req.getResContentType()))
            s3UrlRequest.addRequestParameter(Constants.PARAM_RES_CONTENT_TYPE, req.getResContentType());

        return s3UrlRequest;
    }
}
