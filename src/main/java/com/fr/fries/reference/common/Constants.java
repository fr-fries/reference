package com.fr.fries.reference.common;

public class Constants {
    public static final String HEADER_USER_ID = "x-su-user-id";
    public static final String HEADER_APP_ID = "x-su-app-id";
    public static final String HEADER_SU_CONTENT_TYPE = "x-su-content-type";
    public static final String HEADER_SU_CONTENT_LENGTH = "x-su-content-length";
    public static final String HEADER_RESPONSE_FILENAME = "x-su-response-filename";
    public static final String HEADER_RESPONSE_CONTENT_TYPE = "x-su-response-content-type";
    public static final String HEADER_CONTENT_LENGTH = "content-length";

    public static final String PARAM_CID = "cid";
    public static final String PARAM_RES_CONTENT_TYPE = "response-content-type";
    public static final String PARAM_RES_CONTENT_DISPOSITION = "response-content-disposition";

    public static final String ATTR_RCODE_PREFIX = "100";
    public static final String ATTR_API_NAME = "api_name";
    public static final String ATTR_APP_ID = "app_id";
    public static final String ATTR_USER_ID = "user_id";
    public static final String ATTR_OBJECT_ID = "object_id";
    public static final String ATTR_TTL = "ttl";
    public static final String ATTR_UPLOAD_INFO = "upload_info";
    public static final String ATTR_DOWNLOAD_INFO = "download_info";
    public static final String ATTR_STATUS = "status";
    public static final String ATTR_MESSAGE = "message";
    public static final String ATTR_RETURN_CODE = "rcode";
    public static final String ATTR_RETURN_MESSAGE = "rmsg";
    public static final String ATTR_ERROR = "error";
    public static final String ATTR_CONTENT_TYPE = "content_type";
    public static final String ATTR_CONTENT_LENGTH = "content_length";
    public static final String ATTR_RES_FILENAME = "res_filename";
    public static final String ATTR_RES_CONTENT_TYPE = "res_content_type";
    public static final String ATTR_ATTACHMENT = "attachment;filename=";
    public static final String ATTR_NO_MESSAGE_AVAILABLE = "No message available";

    public static final String URI_CREATE_UPLOAD_URL = "/v1/objects";
    public static final String URI_GET_DOWNLOAD_URL = "/v1/objects/{objectId}/signed";
    public static final String URI_DELETE_OBJECT = "/v1/objects/{objectId}";
    public static final String URI_ADMIN_API = "/health";
    public static final String URI_HEALTH_UP = "/health/up";
    public static final String URI_HEALTH_DOWN = "/health/down";
    public static final String URI_HEALTH_SHUTDOWN = "/health/shutdown";
    public static final String URI_HEALTH_CHECK = "/index.html";

    public static final String API_V1_CREATE_UPLOAD_URL = "create_upload_url";
    public static final String API_V1_GET_DOWNLOAD_URL = "get_download_url";
    public static final String API_V1_DELETE_OBJECT = "delete_object";
    public static final String API_ADMIN = "admin_api";
    public static final String API_NOT_IMPLEMENT = "not_implement_api";

    public static final String VALID_MSG_USER_ID_NOT_BLANK =
            "Request header 'x-su-user-id' must not be null and must contain at least one non-whitespace character";
    public static final String VALID_MSG_USER_ID_SIZE = "Request header 'x-su-user-id' must be between 1 and 36";
    public static final String VALID_MSG_CONTENT_TYPE_NOT_BLANK = "Request header 'x-su-content-type' must not be null";
    public static final String VALID_MSG_CONTENT_LENGTH_MIN =
            "Request header 'x-su-content-length' must be a number whose value must be higher or equal to 1";
    public static final String VALID_MSG_CID_SIZE = "Request parameter 'cid' must have 10 characters";
    public static final String VALID_MSG_OBJECT_ID_SIZE = "Path variable 'object_id' must have 36 characters";

    public static final int RT_OK = 0;
    public static final int RT_ERROR = 1;

    private Constants() {
    }
}
