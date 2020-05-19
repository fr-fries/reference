package com.fr.fries.reference.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fr.fries.reference.BaseTest;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.config.policy.MakeUrlPolicy;
import com.fr.fries.reference.config.policy.ObjectPolicy;
import com.fr.fries.reference.error.SignUrlException;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.lang.reflect.Method;

import static com.fr.fries.reference.TestVariables.cid_normal;
import static com.fr.fries.reference.TestVariables.cid_notInPolicy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class PolicyConfigTest extends BaseTest {

    @Value("classpath:policies/*")
    private Resource[] localResources;

    @Value("classpath:testPolicies/test_invalid_1.json")
    private Resource invalidPolicyResource1;

    @Value("classpath:testPolicies/test_invalid_2.json")
    private Resource invalidPolicyResource2;

    @Value("classpath:stg-aws-ew1/policies/*")
    private Resource[] stgAwsEw1Resources;

    @Value("classpath:stg-aws-an2/policies/*")
    private Resource[] stgAwsAn2Resources;

    @Value("classpath:prd-aws-ew1/policies/*")
    private Resource[] prdAwsEw1Resources;

    @Value("classpath:prd-aws-uw2/policies/*")
    private Resource[] prdAwsUw2Resources;

    @Value("classpath:prd-aws-as1/policies/*")
    private Resource[] prdAwsAs1Resources;

    @Value("classpath:prd-aws-an2/policies/*")
    private Resource[] prdAwsAn2Resources;

    @Value("classpath:prd-aws-cn1/policies/*")
    private Resource[] prdAwsCn1Resources;

    @Test
    public void getObjectPolicy_whenCidIsNotInPolicy_then114905() {
        ObjectPolicy objectPolicy = null;

        try {
            objectPolicy = policyConfig.getObjectPolicy(cid_notInPolicy);
        } catch (SignUrlException e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getResponseCode(), sameInstance(ResponseCode.INTERNAL_POLICY_ERROR));
        }

        assertThat(objectPolicy, nullValue());
    }

    @Test
    public void getMakeUrlPolicy_whenCidIsNotInPolicy_then114905() {
        MakeUrlPolicy makeUrlPolicy = null;

        try {
            makeUrlPolicy = policyConfig.getMakeUrlPolicy(cid_notInPolicy);
        } catch (SignUrlException e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getResponseCode(), sameInstance(ResponseCode.INTERNAL_POLICY_ERROR));
        }

        assertThat(makeUrlPolicy, nullValue());
    }

    @Test
    public void loadPolicyByCategory_whenDurationTypeInvalid_thenJsonMappingException() throws Exception {
        Method method = PolicyConfig.class.getDeclaredMethod("loadPolicyByCategory", Resource.class);
        method.setAccessible(true);

        log.debug("target resource: {}", invalidPolicyResource1.getFilename());
        try {
            method.invoke(policyConfig, invalidPolicyResource1);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getCause(), instanceOf(JsonMappingException.class));
        }
    }

    @Test
    public void loadPolicyByCategory_whenSignerTypeInvalid_thenJsonMappingException() throws Exception {
        Method method = PolicyConfig.class.getDeclaredMethod("loadPolicyByCategory", Resource.class);
        method.setAccessible(true);

        log.debug("target resource: {}", invalidPolicyResource2.getFilename());
        try {
            method.invoke(policyConfig, invalidPolicyResource2);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            assertThat(e.getCause(), instanceOf(JsonMappingException.class));
        }
    }

    @Test
    public void loadPolicyByCategory_whenNormal_thenSuccess() throws Exception {
        Method method = PolicyConfig.class.getDeclaredMethod("loadPolicyByCategory", Resource.class);
        method.setAccessible(true);

        log.debug("target resources: {}, count: {}", "localResources", localResources.length);
        assertThat(localResources.length, not(0));
        for (Resource resource : localResources) {
            if (resource.getFilename() != null && resource.getFilename().contains("test_invalid"))
                continue;
            method.invoke(policyConfig, resource);
        }

        log.debug("target resources: {}, count: {}", "stgAwsEw1Resources", stgAwsEw1Resources.length);
        assertThat(stgAwsEw1Resources.length, not(0));
        for (Resource resource : stgAwsEw1Resources) {
            method.invoke(policyConfig, resource);
        }

        log.debug("target resources: {}, count: {}", "stgAwsAn2Resources", stgAwsAn2Resources.length);
        assertThat(stgAwsAn2Resources.length, not(0));
        for (Resource resource : stgAwsAn2Resources) {
            method.invoke(policyConfig, resource);
        }

        log.debug("target resources: {}, count: {}", "prdAwsEw1Resources", prdAwsEw1Resources.length);
        assertThat(prdAwsEw1Resources.length, not(0));
        for (Resource resource : prdAwsEw1Resources) {
            method.invoke(policyConfig, resource);
        }

        log.debug("target resources: {}, count: {}", "prdAwsUw2Resources", prdAwsUw2Resources.length);
        assertThat(prdAwsUw2Resources.length, not(0));
        for (Resource resource : prdAwsUw2Resources) {
            method.invoke(policyConfig, resource);
        }

        log.debug("target resources: {}, count: {}", "prdAwsAs1Resources", prdAwsAs1Resources.length);
        assertThat(prdAwsAs1Resources.length, not(0));
        for (Resource resource : prdAwsAs1Resources) {
            method.invoke(policyConfig, resource);
        }

        log.debug("target resources: {}, count: {}", "prdAwsAn2Resources", prdAwsAn2Resources.length);
        assertThat(prdAwsAn2Resources.length, not(0));
        for (Resource resource : prdAwsAn2Resources) {
            method.invoke(policyConfig, resource);
        }

        log.debug("target resources: {}, count: {}", "prdAwsCn1Resources", prdAwsCn1Resources.length);
        assertThat(prdAwsCn1Resources.length, not(0));
        for (Resource resource : prdAwsCn1Resources) {
            method.invoke(policyConfig, resource);
        }

        MatcherAssert.assertThat(policyConfig.getObjectPolicy(cid_normal), notNullValue());
        MatcherAssert.assertThat(policyConfig.getMakeUrlPolicy(cid_normal), notNullValue());
    }
}
