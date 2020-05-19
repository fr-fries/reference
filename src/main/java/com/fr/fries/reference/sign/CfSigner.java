package com.fr.fries.reference.sign;

import com.amazonaws.auth.RSA;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.Base64;
import com.fr.fries.reference.common.Constants;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.config.PolicyConfig;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import com.fr.fries.reference.sign.object.PresignedUrl;
import com.fr.fries.reference.sign.object.SignedUrl;
import com.fr.fries.reference.sign.request.SignedUrlRequest;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Setter
@ConfigurationProperties(prefix = "cf")
public class CfSigner extends Signer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String keyPairId;
    private String keyResource;
    private PrivateKey privateKey;

    public CfSigner(PolicyConfig policyConfig, AmazonS3 s3Client) {
        super(policyConfig, s3Client);
    }

    @PostConstruct
    void init() throws SignUrlException {
        try {
            privateKey = RSA.privateKeyFromPKCS8(Base64.decode(keyResource));
        } catch (IllegalArgumentException | InvalidKeySpecException | NullPointerException e) {
            log.error("[CfSigner] Get cloudfront private key failed.");
            throw new SignUrlException(ResponseCode.INTERNAL_CF_SIGNER_ERROR, e);
        }
    }

    public PresignedUrl generateSignedUrl(UrlPolicy urlPolicy, String objectKey, Date reqDate)
            throws SignUrlException {
        try {
            Date expiration = new Date(urlPolicy.getExpiresMillis(reqDate.getTime()));
            String resourcePath = SignerUtils.generateResourcePath(
                    SignerUtils.Protocol.https, urlPolicy.getDomain(), objectKey);
            String signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    resourcePath, keyPairId, privateKey, expiration);

            return new PresignedUrl(urlPolicy.getMethod().name(), signedUrl, expiration.getTime());
        } catch (Exception e) {
            log.error("[CfSigner] generate cloudfront signed url error {} {}", urlPolicy.getDomain(), objectKey);
            throw new SignUrlException(ResponseCode.INTERNAL_CF_SIGNER_ERROR, e);
        }
    }

    public SignedUrl generateSignedUrl(SignedUrlRequest req) throws SignUrlException {
        try {
            String resourcePath = SignerUtils.generateResourcePath(
                    SignerUtils.Protocol.https, req.getDomain(), req.getKey());
            String queryString = getQueryParamString(req.getResFilename(), req.getResContentType());
            String signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    resourcePath + queryString, keyPairId, privateKey, new Date(req.getExpireTime()));

            return new SignedUrl(signedUrl, req.getExpireTime());
        } catch (Exception e) {
            log.error("[CfSigner] generate cloudfront signed url error {} {} {}",
                    req.getDomain(), req.getKey(), req.getExpireTime());
            throw new SignUrlException(ResponseCode.INTERNAL_CF_SIGNER_ERROR, e);
        }
    }

    private String getQueryParamString(String resFilename, String resContentType) {
        Map<String, String> customQueryParameters = new HashMap<>();

        if (StringUtils.isNotBlank(resFilename)) {
            customQueryParameters.put(Constants.PARAM_RES_CONTENT_DISPOSITION,
                    URLEncoder.encode(Constants.ATTR_ATTACHMENT + resFilename));
        }

        if (StringUtils.isNotBlank(resContentType))
            customQueryParameters.put(Constants.PARAM_RES_CONTENT_TYPE, resContentType);

        return concatQueryParameters(customQueryParameters);
    }

    private String concatQueryParameters(Map<String, String> customQueryParameters) {
        String loopDelimiter = "?";
        StringBuilder buf = new StringBuilder();

        for (Map.Entry<String, String> customQueryParameter : customQueryParameters.entrySet()) {
            if (StringUtils.isBlank(customQueryParameter.getValue()))
                continue;

            buf.append(loopDelimiter);
            buf.append(customQueryParameter.getKey());
            buf.append("=");
            buf.append(customQueryParameter.getValue());

            loopDelimiter = "&";
        }

        return buf.toString();
    }
}
