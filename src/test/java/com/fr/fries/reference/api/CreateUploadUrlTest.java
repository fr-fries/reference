package com.fr.fries.reference.api;

import com.fr.fries.reference.BaseTest;
import com.fr.fries.reference.common.Constants;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.config.policy.ObjectPolicy;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.error.SignUrlException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.fr.fries.reference.TestVariables.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public class CreateUploadUrlTest extends BaseTest {

    @Test
    public void createUploadUrl_whenNormal_then200OK() throws SignUrlException {
        ObjectPolicy objectPolicy = policyConfig.getObjectPolicy(cid);
        UrlPolicy uploadUrlPolicy = policyConfig.getMakeUrlPolicy(cid).getUploadUrl();

        v1CreateUploadUrl(appId, userId, contentType, contentLength, cid)
                .apply(document("createUploadUrl",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("cid").attributes(setType("String (10)")).description("Content Id")),
                        requestHeaders(
                                headerWithName("x-su-user-id").attributes(setType("String (1-36)")).description("User ID"),
                                headerWithName("x-su-app-id").attributes(setType("String (10)")).description("Application ID"),
                                headerWithName("x-su-content-type").attributes(setType("Content-Type")).description("Content Type of Object to be Uploaded"),
                                headerWithName("x-su-content-length").attributes(setType("Number (1-max)")).description("Content Length of Object to be Uploaded"),
                                headerWithName("x-su-trid").attributes(setType("String")).description("Transaction Id").optional()),
                        responseFields(
                                fieldWithPath("rcode").type(JsonFieldType.NUMBER).description("Error Code").optional(),
                                fieldWithPath("rmsg").type(JsonFieldType.STRING).description("Error Message").optional(),
                                fieldWithPath("object_id").attributes(setType("String (36)")).description("Object Id"),
                                fieldWithPath("ttl").type(JsonFieldType.NUMBER).description("Time-To-Live 'DAY' of Object"),
                                fieldWithPath("upload_info.url").type(JsonFieldType.STRING).description("Upload URL"),
                                fieldWithPath("upload_info.expires").type(JsonFieldType.NUMBER).description("Upload URL Expiry"))
                ))
                .statusCode(HttpStatus.OK.value())
                .body(Constants.ATTR_RETURN_CODE, nullValue())
                .body(Constants.ATTR_RETURN_MESSAGE, nullValue())
                .body(Constants.ATTR_OBJECT_ID, instanceOf(String.class))
                .body(Constants.ATTR_TTL, instanceOf(Integer.class))
                .body(Constants.ATTR_TTL, equalTo((int) objectPolicy.getTtl().toDays()))
                .body(Constants.ATTR_UPLOAD_INFO, notNullValue())
                .body("upload_info.url", instanceOf(String.class))
                .body("upload_info.url", startsWith(url_starts))
                .body("upload_info.url", containsString(url_contentType))
                .body("upload_info.url", containsString(url_contentLength))
                .body("upload_info.url", containsString(cid))
                .body("upload_info.url", containsString(uploadUrlPolicy.getDomain()))
                .body("upload_info.expires", instanceOf(Long.class))
                .body("upload_info.expires", greaterThanOrEqualTo(uploadUrlPolicy.getExpiresMillis(requestTime)))
                .body(Constants.ATTR_DOWNLOAD_INFO, nullValue());
    }

    @Test
    public void createUploadUrl_whenUserIdNotExist_then114400() {
        v1CreateUploadUrl(appId, null, contentType, contentLength, cid)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Missing request header 'x-su-user-id' " +
                        "for method parameter of type String"));
    }

    @Test
    public void createUploadUrl_whenUserIdIsBlank_then114400() {
        v1CreateUploadUrl(appId, userId_blank, contentType, contentLength, cid)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Request header 'x-su-user-id' must not be null " +
                        "and must contain at least one non-whitespace character"));
    }

    @Test
    public void createUploadUrl_whenUserIdIsInvalid_then114400() {
        v1CreateUploadUrl(appId, userId_invalid, contentType, contentLength, cid)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Request header 'x-su-user-id' must be between 1 and 36"));
    }

    @Test
    public void createUploadUrl_whenContentTypeNotExist_then114400() {
        v1CreateUploadUrl(appId, userId, null, contentLength, cid_normal)
                .apply(document("createUploadUrl_contentTypeNotExist",
                        getDocumentRequest(),
                        getDocumentResponse()))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Missing request header 'x-su-content-type' " +
                        "for method parameter of type String"));
    }

    @Test
    public void createUploadUrl_whenContentLengthNotExist_then114400() {
        v1CreateUploadUrl(appId, userId, contentType, null, cid_normal)
                .apply(document("createUploadUrl_contentLengthNotExist",
                        getDocumentRequest(),
                        getDocumentResponse()))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Missing request header 'x-su-content-length' " +
                        "for method parameter of type Long"));
    }

    @Test
    public void createUploadUrl_whenContentLengthIsZero_then114400() {
        v1CreateUploadUrl(appId, userId, contentType, contentLength_zero, cid_normal)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Request header 'x-su-content-length' must be a number " +
                        "whose value must be higher or equal to 1"));
    }

    @Test
    public void createUploadUrl_whenContentLengthExceedsMax_then114400() {
        v1CreateUploadUrl(appId, userId, contentType, contentLength_exceeds_max, cid_normal)
                .apply(document("createUploadUrl_exceedMaxLength",
                        getDocumentRequest(),
                        getDocumentResponse()))
                .statusCode(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.REQUEST_CONTENT_LENGTH_TOO_LONG.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Request header 'x-su-content-length' is too long. " +
                        "It must not exceed the maximum assigned value, 1073741824B"));
    }

    @Test
    public void createUploadUrl_whenCidNotExist_then114400() {
        v1CreateUploadUrl(appId, userId, contentType, contentLength, null)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Required String parameter 'cid' is not present"));
    }

    @Test
    public void createUploadUrl_whenCidIsInvalid_then114400() {
        v1CreateUploadUrl(appId, userId, contentType, contentLength, cid_invalid)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Request parameter 'cid' must have 10 characters"));
    }

    @Test
    public void createUploadUrl_whenCidIsNotInPolicy_then114905() {
        v1CreateUploadUrl(appId, userId, contentType, contentLength, cid_notInPolicy)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.INTERNAL_POLICY_ERROR.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Policy error"));
    }
}
