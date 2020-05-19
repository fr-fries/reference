package com.fr.fries.reference.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.util.Base64;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.error.SignUrlException;
import org.apache.http.HttpHost;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;

@Configuration
public class EncryptionConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(@Value("${kms.cipher:#{null}}") String kmsCipherKey,
                                           @Value("${sign.proxy:#{null}}") String proxy) throws SignUrlException {
        log.debug("[EncryptionConfig] Start encryption configuration");
        log.debug("[EncryptionConfig] cipher : {}, proxy : {}", kmsCipherKey, proxy);

        if (kmsCipherKey != null) {
            try {
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setPassword(getKmsKey(kmsCipherKey, proxy));
                encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");

                return encryptor;
            } catch (Exception e) {
                throw new SignUrlException(ResponseCode.INTERNAL_KMS_ERROR, e);
            }
        }

        return null;
    }

    private String getKmsKey(String kmsCipherKey, String proxy) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                .withClientConfiguration(createConfig(proxy)).build();
        DecryptRequest decryptRequest = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(Base64.decode(kmsCipherKey)));
        DecryptResult decryptResult = kmsClient.decrypt(decryptRequest);

        return new String(Base64.encode(decryptResult.getPlaintext().array()));
    }

    private ClientConfiguration createConfig(String proxy) {
        if (proxy == null)
            return null;

        HttpHost proxyHost = HttpHost.create(proxy);
        return new ClientConfiguration()
                .withProxyHost(proxyHost.getHostName())
                .withProxyPort(proxyHost.getPort());
    }
}
