package com.fr.fries.reference.api;

import com.fr.fries.reference.BaseTest;
import com.fr.fries.reference.common.Constants;
import com.fr.fries.reference.common.ResponseCode;
import com.fr.fries.reference.config.policy.UrlPolicy;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.fr.fries.reference.TestVariables.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class GetDownloadUrlTest extends BaseTest {

    @Test
    public void getDownloadUrl_whenNormal_then200OK() throws Exception {
        UrlPolicy downloadUrlPolicy = policyConfig.getMakeUrlPolicy(cid).getDownloadUrl();

        v1GetDownloadUrl(appId, userId, null, null, cid, objectId, true)
                .apply(document("getDownloadUrl",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("objectId").attributes(setType("String (36)")).description("Object Id")),
                        requestParameters(
                                parameterWithName("cid").attributes(setType("String (10)")).description("Content Id")),
                        requestHeaders(
                                headerWithName("x-su-user-id").attributes(setType("String (1-36)")).description("User ID"),
                                headerWithName("x-su-app-id").attributes(setType("String (10)")).description("Application ID"),
                                headerWithName("x-su-response-filename").attributes(setType("String (1-64)")).description("Filename of Object to be Downloaded").optional(),
                                headerWithName("x-su-response-content-type").attributes(setType("Content-Type")).description("Content Type of Object to be Downloaded").optional(),
                                headerWithName("x-su-trid").attributes(setType("String")).description("Transaction Id").optional()),
                        responseFields(
                                fieldWithPath("rcode").type(JsonFieldType.NUMBER).description("Error Code").optional(),
                                fieldWithPath("rmsg").type(JsonFieldType.STRING).description("Error Message").optional(),
                                fieldWithPath("object_id").attributes(setType("String (36)")).description("Object Id"),
                                fieldWithPath("download_info.url").type(JsonFieldType.STRING).description("Download URL"),
                                fieldWithPath("download_info.expires").type(JsonFieldType.NUMBER).description("Download URL Expiry"))
                ))
                .statusCode(HttpStatus.OK.value())
                .body(Constants.ATTR_RETURN_CODE, nullValue())
                .body(Constants.ATTR_RETURN_MESSAGE, nullValue())
                .body(Constants.ATTR_OBJECT_ID, instanceOf(String.class))
                .body(Constants.ATTR_OBJECT_ID, equalTo(objectId))
                .body(Constants.ATTR_TTL, nullValue())
                .body(Constants.ATTR_UPLOAD_INFO, nullValue())
                .body(Constants.ATTR_DOWNLOAD_INFO, notNullValue())
                .body("download_info.url", instanceOf(String.class))
                .body("download_info.url", startsWith(url_starts))
                .body("download_info.url", containsString(cid))
                .body("download_info.url", containsString(objectId))
                .body("download_info.url", containsString(downloadUrlPolicy.getDomain()))
                .body("download_info.expires", instanceOf(Long.class))
                .body("download_info.expires", greaterThanOrEqualTo(downloadUrlPolicy.getExpiresMillis(requestTime)));
    }

    @Test
    public void getDownloadUrl_whenWithCFResFilename_then200OK() throws Exception {
        v1GetDownloadUrl(appId, userId, resFilename_rename, null, cid, objectId, true)
                .statusCode(HttpStatus.OK.value())
                .body(Constants.ATTR_OBJECT_ID, equalTo(objectId))
                .body("download_info.url", containsString(objectId))
                .body("download_info.url", containsString(Constants.PARAM_RES_CONTENT_DISPOSITION))
                .body("download_info.url", containsString(resFilename_rename));
    }

    @Test
    public void getDownloadUrl_whenWithCFResContentType_then200OK() throws Exception {
        v1GetDownloadUrl(appId, userId, null, contentType_wav, cid, objectId, true)
                .statusCode(HttpStatus.OK.value())
                .body(Constants.ATTR_OBJECT_ID, equalTo(objectId))
                .body("download_info.url", containsString(objectId))
                .body("download_info.url", containsString(Constants.PARAM_RES_CONTENT_TYPE))
                .body("download_info.url", containsString(contentType_wav));
    }

    @Test
    public void getDownloadUrl_whenWithS3ResFilename_then200OK() throws Exception {
        v1GetDownloadUrl(appId, userId, resFilename_rename, null, cid_s3_down, objectId, true)
                .statusCode(HttpStatus.OK.value())
                .body(Constants.ATTR_OBJECT_ID, equalTo(objectId))
                .body("download_info.url", containsString(objectId))
                .body("download_info.url", containsString(Constants.PARAM_RES_CONTENT_DISPOSITION))
                .body("download_info.url", containsString(resFilename_rename));
    }

    @Test
    public void getDownloadUrl_whenIfNotGdprResFilename_then200OK() throws Exception {
        v1GetDownloadUrl(appId, userId, null, null, cid, objectId, true)
                .statusCode(HttpStatus.OK.value())
                .body(Constants.ATTR_OBJECT_ID, equalTo(objectId))
                .body("download_info.url", containsString(objectId));
    }

    @Test
    public void getDownloadUrl_whenIfGdprResFilenameNotExist_then114400() throws Exception {
        v1GetDownloadUrl(appId, userId, null, null, cid_normal, objectId, true)
                .apply(document("getDownloadUrl_filenameNotExist",
                        getDocumentRequest(),
                        getDocumentResponse()))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.RESPONSE_FILENAME_REQUIRED.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Missing request header 'x-su-response-filename' " +
                        "for method parameter of type String"));
    }

    @Test
    public void getDownloadUrl_whenWithS3ResContentType_then200OK() throws Exception {
        v1GetDownloadUrl(appId, userId, null, contentType_wav, cid_s3_down, objectId, true)
                .statusCode(HttpStatus.OK.value())
                .body(Constants.ATTR_OBJECT_ID, equalTo(objectId))
                .body("download_info.url", containsString(objectId))
                .body("download_info.url", containsString(Constants.PARAM_RES_CONTENT_TYPE))
                .body("download_info.url", containsString(contentType_wav_urlEncode));
    }

    @Test
    public void getDownloadUrl_whenObjectNotUploaded_then114404() throws Exception {
        v1GetDownloadUrl(appId, userId, cid, getRandomUUID())
                .apply(document("getDownloadUrl_objectNotExist",
                        getDocumentRequest(),
                        getDocumentResponse()))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.OBJECT_NOT_FOUND.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Object does not exist"));
    }

    @Test
    public void getDownloadUrl_whenUserIdNotExist_then114400() throws Exception {
        v1GetDownloadUrl(appId, null, cid, objectId)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Missing request header 'x-su-user-id' " +
                        "for method parameter of type String"));
    }

    @Test
    public void getDownloadUrl_whenUserIdIsBlank_then114400() throws Exception {
        v1GetDownloadUrl(appId, userId_blank, cid, objectId)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Request header 'x-su-user-id' must not be null " +
                        "and must contain at least one non-whitespace character"));
    }

    @Test
    public void getDownloadUrl_whenUserIdIsInvalid_then114400() throws Exception {
        v1GetDownloadUrl(appId, userId_invalid, cid, objectId)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Request header 'x-su-user-id' must be between 1 and 36"));
    }

    @Test
    public void getDownloadUrl_whenCidNotExist_then114400() throws Exception {
        v1GetDownloadUrl(appId, userId, null, objectId)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Required String parameter 'cid' is not present"));
    }

    @Test
    public void getDownloadUrl_whenCidIsInvalid_then114400() throws Exception {
        v1GetDownloadUrl(appId, userId, cid_invalid, objectId)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Request parameter 'cid' must have 10 characters"));
    }

    @Test
    public void getDownloadUrl_whenCidIsNotInPolicy_then114905() throws Exception {
        v1GetDownloadUrl(appId, userId, cid_notInPolicy, objectId)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.INTERNAL_POLICY_ERROR.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Policy error"));
    }

    @Test
    public void getDownloadUrl_whenObjectIdIsBlank_then114400() throws Exception {
        v1GetDownloadUrl(appId, userId, cid, objectId_blank)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Path variable 'object_id' must have 36 characters"));
    }

    @Test
    public void getDownloadUrl_whenObjectIdIsInvalid_then114400() throws Exception {
        v1GetDownloadUrl(appId, userId, cid, objectId_invalid_35)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Path variable 'object_id' must have 36 characters"));

        v1GetDownloadUrl(appId, userId, cid, objectId_invalid_37)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Constants.ATTR_RETURN_CODE, equalTo((int) ResponseCode.BAD_REQUEST.getCode()))
                .body(Constants.ATTR_RETURN_MESSAGE, equalTo("Bad Request: Path variable 'object_id' must have 36 characters"));
    }
}
