package com.fr.fries.reference.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Setter;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@ConfigurationProperties(prefix = "s3")
public class S3Config {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String accessKey;
    private String secretKey;
    private Integer connectionTimeout;
    private Integer maxConnections;
    private Integer maxErrorRetry;
    private String protocol;
    private String region;

    @Bean
    public AmazonS3 amazonS3Client(@Value("${sign.proxy:#{null}}") String proxy) {
        log.debug("[S3Config] Start s3 configuration");
        log.debug("[S3Config] accessKey : {}", accessKey);
        AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration config = new ClientConfiguration();
        config.setConnectionTimeout(connectionTimeout);
        config.setMaxConnections(maxConnections);
        config.setMaxErrorRetry(maxErrorRetry);
        config.setProtocol(Protocol.valueOf(protocol));
        config.setSocketTimeout(connectionTimeout);

        log.debug("[S3Config] proxy: {}", proxy);
        if (proxy != null) {
            HttpHost proxyHost = HttpHost.create(proxy);
            config.setProtocol(Protocol.valueOf(proxyHost.getSchemeName().toUpperCase()));
            config.setProxyHost(proxyHost.getHostName());
            config.setProxyPort(proxyHost.getPort());
        }

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(myCredentials))
                .withClientConfiguration(config)
                .withRegion(region)
                .build();
    }
}
