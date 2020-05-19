package com.fr.fries.reference.service;

import com.amazonaws.services.s3.AmazonS3;
import com.fr.fries.reference.config.PolicyConfig;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import com.fr.fries.reference.sign.Signer;
import com.fr.fries.reference.sign.SignerFactory;
import com.fr.fries.reference.sign.object.SignedUrl;
import com.fr.fries.reference.sign.request.SignedUrlRequest;
import com.fr.fries.reference.utils.KeyUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUrlServiceImpl {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PolicyConfig policyConfig;
    private final SignerFactory signerFactory;
    private final AmazonS3 s3Client;

    public SignedUrl getUploadSignedUrl(SignedUrlRequest req) throws SignUrlException {
        log.debug("[SignUrlService] Get Upload Signed URL start. unitId: {}, cid: {}", req.getUserId(), req.getCid());

        UrlPolicy uploadUrlPolicy = policyConfig.getUploadUrlPolicy(req.getCid());
        req.setUrlPolicy(uploadUrlPolicy);
        req.setTtl(policyConfig.getObjectTtl(req.getCid()));
        log.debug("[SignUrlService] cid: {}, uploadUrlPolicy : {}, ttl: {}",
                req.getCid(), uploadUrlPolicy.toString(), req.getTtl());

        Signer signer = signerFactory.getSigner(req.getType());
        signer.validatePutSignedUrlRequest(req);
        SignedUrl signedUrl = signer.generateSignedUrl(req);
        log.debug("[SignUrlService] signedUrl: {}, expires: {}", signedUrl.getUrl(), signedUrl.getExpires());

        return signedUrl;
    }

    public SignedUrl getDownloadSignedUrl(SignedUrlRequest req) throws SignUrlException {
        log.debug("[SignUrlService] Get Download Signed URL start. unitId: {}, cid: {}, objectId: {}",
                req.getUserId(), req.getCid(), req.getObjectId());

        UrlPolicy downloadUrlPolicy = policyConfig.getDownloadUrlPolicy(req.getCid());
        req.setUrlPolicy(downloadUrlPolicy);
        log.debug("[SignUrlService] cid: {}, downloadUrlPolicy : {}", req.getCid(), downloadUrlPolicy.toString());

        Signer signer = signerFactory.getSigner(req.getType());
        signer.validateGetSignedUrlRequest(req);
        SignedUrl signedUrl = signer.generateSignedUrl(req);
        log.debug("[SignUrlService] signedUrl: {}, expires: {}", signedUrl.getUrl(), signedUrl.getExpires());

        return signedUrl;
    }

    public void deleteObject(String userId, String cid, String objectId) throws SignUrlException {
        log.debug("[SignUrlService] Delete Object start. unitId: {}, cid: {}, objectId: {}", userId, cid, objectId);

        String originBucket = policyConfig.getOriginBucket(cid);
        String key = KeyUtils.getS3Key(cid, userId, objectId);

        s3Client.deleteObject(originBucket, key);
    }
}
