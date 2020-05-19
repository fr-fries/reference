package com.fr.fries.reference.utils;

import com.fasterxml.uuid.Generators;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.error.SignUrlException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class KeyUtils {

    private static final String ALGORITHM = "SHA-256";
    private static final Logger log = LoggerFactory.getLogger(KeyUtils.class);

    private KeyUtils() {
    }

    public static String getS3Key(String cid, String uid, String appId, String fileName) throws SignUrlException {
        return StringUtils.joinWith("/", cid, getHexString(uid), getHexString(appId), fileName);
    }

    public static String getS3Key(String cid, String uid, String objectId) throws SignUrlException {
        return StringUtils.joinWith("/", cid, getHexString(uid), objectId);
    }

    public static String generateObjectId() {
        return Generators.timeBasedGenerator().generate().toString();
    }

    static String getHexString(String str) throws SignUrlException {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] byteData = digest.digest(str.getBytes(StandardCharsets.UTF_8));

            return Hex.encodeHexString(byteData);
        } catch (NoSuchAlgorithmException e) {
            log.error("[KeyUtils] Create message digest failed {} algorithm.", ALGORITHM);
            throw new SignUrlException(ResponseCode.INTERNAL_MESSAGE_DIGEST_ERROR, e);
        }
    }
}
