package com.fr.fries.reference;

import com.fr.fries.reference.config.PolicyConfig;
import com.fr.fries.reference.service.SignUrlServiceImpl;
import com.fr.fries.reference.sign.object.SignedUrl;
import com.fr.fries.reference.sign.request.SignedUrlRequest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static com.fr.fries.reference.TestVariables.*;
import static com.fr.fries.reference.common.Constants.*;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("local")
public class BaseTest {

    @Autowired
    public PolicyConfig policyConfig;

    @Autowired
    public SignUrlServiceImpl signUrlService;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    public RequestSpecification spec;

    protected String fileName;
    protected String objectId;
    protected long requestTime;
    protected Date reqDate;
    protected String appId = appId_iot;
    protected String userId = userId_normal;
    protected String cid = cid_normal;
    protected String body = body_data;
    protected String contentType = contentType_textPlain;
    protected long contentLength = body_data.length();

    private RestTemplate restTemplate;

    @Autowired
    private WebApplicationContext context;

    @Value("${sign.proxy:#{null}}")
    private String proxy;

    public static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(modifyUris()
                        .scheme("https")
                        .host("fr.fries.com")
                        .port(8080),
                removeHeaders("Content-Type"),
                prettyPrint());
    }

    public static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    public static Attributes.Attribute setType(String value) {
        return key("type").value(value);
    }

    @Before
    public void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .build());

        RestAssuredMockMvc.config = RestAssuredMockMvc.config()
                .encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (proxy != null) {
            String[] proxyStr = proxy.split(":");
            String proxyHost = proxyStr[1].substring(2);
            int proxyPort = Integer.parseInt(proxyStr[2]);

            Proxy proxyConfig = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            requestFactory.setProxy(proxyConfig);
        }
        restTemplate = new RestTemplate(requestFactory);

        this.fileName = this.objectId = getRandomUUID();
        this.requestTime = System.currentTimeMillis();
        this.reqDate = new Date();
        log.debug("fileName|objectId: {}, requestTime: {}", fileName, requestTime);
    }

    @Test
    public void contextLoads() {
        assertThat(policyConfig, notNullValue());
        assertThat(signUrlService, notNullValue());
        assertThat(restTemplate, notNullValue());
        assertThat(context, notNullValue());
    }

    //==================================================================================================================
    // utils
    //==================================================================================================================
    protected String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    //==================================================================================================================
    // make request
    //==================================================================================================================
    protected MockMvcRequestSpecification request() {
        return given();
    }

    //==================================================================================================================
    // server
    //==================================================================================================================
    protected void httpRequest(String url, String contentType, String body) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(body.length());
        headers.setContentType(MediaType.parseMediaType(contentType));
        HttpEntity request = new HttpEntity<>(body, headers);

        restTemplate.exchange(new URI(url), HttpMethod.PUT, request, Void.class);
    }

    protected String httpRequest(String url) throws Exception {
        return restTemplate.getForObject(new URI(url), String.class);
    }

    //==================================================================================================================
    // server
    //==================================================================================================================
    protected ValidatableMockMvcResponse v1CreateUploadUrl(String appId, String userId, String contentType,
                                                           Long contentLength, String cid) {
        MockMvcRequestSpecification request = given();

        if (userId != null)
            request.header(HEADER_USER_ID, userId);

        if (appId != null)
            request.header(HEADER_APP_ID, appId);

        if (contentType != null)
            request.header(HEADER_SU_CONTENT_TYPE, contentType);

        if (contentLength != null)
            request.header(HEADER_SU_CONTENT_LENGTH, contentLength);

        if (cid != null)
            request.param(PARAM_CID, cid);

        return request.when().post(URI_CREATE_UPLOAD_URL).then().apply(print());
    }

    protected ValidatableMockMvcResponse v1GetDownloadUrl(String appId, String userId, String cid,
                                                          String objectId) throws Exception {
        return v1GetDownloadUrl(appId, userId, null, null, cid, objectId, false);
    }

    protected ValidatableMockMvcResponse v1GetDownloadUrl(String appId, String userId, String resFilename,
                                                          String resContentType, String cid, String objectId,
                                                          boolean preUpload) throws Exception {
        if (preUpload) {
            SignedUrlRequest signedUrlReq = new SignedUrlRequest(userId, cid, contentType, contentLength);
            signedUrlReq.setObjectId(objectId);
            SignedUrl uploadUrl = signUrlService.getUploadSignedUrl(signedUrlReq);
            httpRequest(uploadUrl.getUrl(), contentType, body);
        }

        MockMvcRequestSpecification request = given();

        if (userId != null)
            request.header(HEADER_USER_ID, userId);

        if (appId != null)
            request.header(HEADER_APP_ID, appId);

        if (resFilename != null)
            request.header(HEADER_RESPONSE_FILENAME, resFilename);

        if (resContentType != null)
            request.header(HEADER_RESPONSE_CONTENT_TYPE, resContentType);

        if (cid != null)
            request.param(PARAM_CID, cid);

        return request.when().get(URI_GET_DOWNLOAD_URL, objectId).then().apply(print());
    }

    protected ValidatableMockMvcResponse v1DeleteObject(String appId, String userId, String cid,
                                                        String objectId) throws Exception {
        return v1DeleteObject(appId, userId, cid, objectId, false);
    }

    protected ValidatableMockMvcResponse v1DeleteObject(String appId, String userId, String cid,
                                                        String objectId, boolean preUpload) throws Exception {
        if (preUpload) {
            SignedUrlRequest signedUrlReq = new SignedUrlRequest(userId, cid, contentType, contentLength);
            signedUrlReq.setObjectId(objectId);
            SignedUrl uploadUrl = signUrlService.getUploadSignedUrl(signedUrlReq);
            httpRequest(uploadUrl.getUrl(), contentType, body);
        }

        MockMvcRequestSpecification request = given();

        if (userId != null)
            request.header(HEADER_USER_ID, userId);

        if (appId != null)
            request.header(HEADER_APP_ID, appId);

        if (cid != null)
            request.param(PARAM_CID, cid);

        return request.when().delete(URI_DELETE_OBJECT, objectId).then().apply(print());
    }
}
