package com.fr.fries.reference.sign;

import com.fr.fries.reference.BaseTest;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import com.fr.fries.reference.sign.object.PresignedUrl;
import com.fr.fries.reference.sign.object.SignedUrl;
import com.fr.fries.reference.sign.request.SignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.fr.fries.reference.TestVariables.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

@Slf4j
public class SignerTest extends BaseTest {

    @Autowired
    S3Signer s3Signer;

    @Autowired
    CfSigner cfSigner;

    @Test
    public void generateS3PresignedUrl_whenS3ClientIsNull_then114901() {
        S3Signer s3Signer = new S3Signer(policyConfig, null);
        PresignedUrl presignedUrl = null;

        try {
            UrlPolicy normalUrlPolicy = policyConfig.getUploadUrlPolicy(cid_normal);

            log.debug("type: {}, domain: {}, date: {}",
                    normalUrlPolicy.getType(), normalUrlPolicy.getDomain(), reqDate);
            presignedUrl = s3Signer.generateSignedUrl(normalUrlPolicy, objectId, reqDate);
        } catch (SignUrlException e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getResponseCode(), sameInstance(ResponseCode.INTERNAL_S3_SIGNER_ERROR));
        }

        assertThat(presignedUrl, nullValue());
    }

    @Test
    public void generateS3SignedUrl_whenS3ClientIsNull_then114901() {
        S3Signer s3Signer = new S3Signer(policyConfig, null);
        SignedUrl signedUrl = null;

        try {
            UrlPolicy normalUrlPolicy = policyConfig.getUploadUrlPolicy(cid_normal);
            SignedUrlRequest req = new SignedUrlRequest(userId_normal, cid_normal, contentType_textPlain, contentLength);
            req.setUrlPolicy(normalUrlPolicy);

            log.debug("type: {}, domain: {}, date: {}",
                    req.getType(), req.getDomain(), reqDate);
            signedUrl = s3Signer.generateSignedUrl(req);
        } catch (SignUrlException e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getResponseCode(), sameInstance(ResponseCode.INTERNAL_S3_SIGNER_ERROR));
        }

        assertThat(signedUrl, nullValue());
    }

    @Test
    public void constructCfSigner_whenCfKeyPairPathIsNull_then114902() {
        CfSigner cfSigner = new CfSigner(policyConfig, null);

        try {
            cfSigner.init();
        } catch (SignUrlException e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getResponseCode(), sameInstance(ResponseCode.INTERNAL_CF_SIGNER_ERROR));
        }
    }

    @Test
    public void generateCfPresignedUrl_whenCfKeyPairIdIsNull_then114902() {
        CfSigner cfSigner = new CfSigner(policyConfig, null);
        PresignedUrl presignedUrl = null;

        try {
            UrlPolicy normalUrlPolicy = policyConfig.getDownloadUrlPolicy(cid_normal);

            log.debug("type: {}, domain: {}, date: {}",
                    normalUrlPolicy.getType(), normalUrlPolicy.getDomain(), reqDate);
            presignedUrl = cfSigner.generateSignedUrl(normalUrlPolicy, objectId, reqDate);
        } catch (SignUrlException e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getResponseCode(), sameInstance(ResponseCode.INTERNAL_CF_SIGNER_ERROR));
        }

        assertThat(presignedUrl, nullValue());
    }

    @Test
    public void generateCfSignedUrl_whenCfKeyPairIdIsNull_then114902() {
        CfSigner cfSigner = new CfSigner(policyConfig, null);
        SignedUrl signedUrl = null;

        try {
            UrlPolicy normalUrlPolicy = policyConfig.getDownloadUrlPolicy(cid_normal);
            SignedUrlRequest req = new SignedUrlRequest(userId_normal, cid_normal, contentType_textPlain, contentLength);
            req.setUrlPolicy(normalUrlPolicy);

            log.debug("type: {}, domain: {}, date: {}",
                    req.getType(), req.getDomain(), reqDate);
            signedUrl = cfSigner.generateSignedUrl(req);
        } catch (SignUrlException e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getResponseCode(), sameInstance(ResponseCode.INTERNAL_CF_SIGNER_ERROR));
        }

        assertThat(signedUrl, nullValue());
    }
}
