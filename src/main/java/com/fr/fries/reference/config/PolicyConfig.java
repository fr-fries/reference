package com.fr.fries.reference.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.fries.reference.SignUrlApplication;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.common.SignerType;
import com.fr.fries.reference.config.policy.MakeUrlPolicy;
import com.fr.fries.reference.config.policy.ObjectPolicy;
import com.fr.fries.reference.config.policy.PolicyInfo;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.unit.DataSize;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

import static com.fr.fries.reference.common.Constants.RT_ERROR;

@Configuration
@RequiredArgsConstructor
public class PolicyConfig {

    private static EnumMap<SignerType, String> defaultDomains = new EnumMap<>(SignerType.class);

    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<String, MakeUrlPolicy> makeUrlPolicies = new HashMap<>();
    private Map<String, ObjectPolicy> objectPolicies = new HashMap<>();
    private List<String> resFilenameRequiredCids = new ArrayList<>();

    @Value("classpath:policies/*")
    private Resource[] policyResources;

    public static String getDefaultDomain(SignerType signerType) {
        return defaultDomains.get(signerType);
    }

    @Autowired
    public void setS3DefaultDomains(@Value("${s3.bucket}") String s3Domain,
                                    @Value("${cf.domain}") String cfDomain) {
        defaultDomains.put(SignerType.S3, s3Domain);
        defaultDomains.put(SignerType.CF, cfDomain);
    }

    @PostConstruct
    void init() {
        log.debug("[PolicyConfig] Start policy configuration");
        try {
            loadPolicyConfig(policyResources);
        } catch (Exception e) {
            SignUrlApplication.shutdown(RT_ERROR);
        }
    }

    public ObjectPolicy getObjectPolicy(String cid) throws SignUrlException {
        if (cid == null || !objectPolicies.containsKey(cid)) {
            log.error("[PolicyConfig] Get object policy failed. cid: {}", cid);
            throw new SignUrlException(ResponseCode.INTERNAL_POLICY_ERROR);
        }

        return objectPolicies.get(cid);
    }

    public DataSize getMaxContentLength(String cid) throws SignUrlException {
        return getObjectPolicy(cid).getMaxContentLength();
    }

    public Duration getObjectTtl(String cid) throws SignUrlException {
        return getObjectPolicy(cid).getTtl();
    }

    public UrlPolicy getUploadUrlPolicy(String cid) throws SignUrlException {
        return getMakeUrlPolicy(cid).getUploadUrl();
    }

    public UrlPolicy getDownloadUrlPolicy(String cid) throws SignUrlException {
        return getMakeUrlPolicy(cid).getDownloadUrl();
    }

    public MakeUrlPolicy getMakeUrlPolicy(String cid) throws SignUrlException {
        if (cid == null || !makeUrlPolicies.containsKey(cid)) {
            log.error("[PolicyConfig] Get make url policy failed. cid: {}", cid);
            throw new SignUrlException(ResponseCode.INTERNAL_POLICY_ERROR);
        }

        return makeUrlPolicies.get(cid);
    }

    public String getOriginBucket(String cid) throws SignUrlException {
        return getMakeUrlPolicy(cid).getUploadUrl().getDomain();
    }

    public boolean isResFilenameRequired(String cid) {
        return resFilenameRequiredCids.contains(cid);
    }

    private void loadPolicyConfig(Resource[] policyResources) throws IOException {
        log.debug("[PolicyConfig] Policy count: {}", policyResources.length);

        for (Resource resource : policyResources) {
            log.debug("[PolicyConfig] path: {}", resource.getURI().toString());
            if (resource.exists() && resource.getFilename() != null)
                loadPolicyByCategory(resource);
        }
    }

    private void loadPolicyByCategory(Resource resource) throws IOException {
        try {
            String categoryId = resource.getFilename();
            log.debug("[PolicyConfig] categoryId: {}, last_modified_time: {}", categoryId, resource.lastModified());

            List<PolicyInfo> policyInfoList = readPolicyResources(resource.getInputStream());
            log.debug("[PolicyConfig] policyInfo: {}", policyInfoList.toString());

            for (PolicyInfo policyInfo : policyInfoList) {
                for (String cid : policyInfo.getCids()) {
                    objectPolicies.put(cid, policyInfo.getObjectPolicy());
                    makeUrlPolicies.put(cid, policyInfo.getMakeUrlPolicy());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private List<PolicyInfo> readPolicyResources(InputStream is) throws IOException {
        return objectMapper.readValue(is, new TypeReference<List<PolicyInfo>>() {
        });
    }
}
