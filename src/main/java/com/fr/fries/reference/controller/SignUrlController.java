package com.fr.fries.reference.controller;

import com.fr.fries.reference.controller.response.GetSignedUrlResponse;
import com.fr.fries.reference.controller.response.ServletResponse;
import com.fr.fries.reference.error.SignUrlException;
import com.fr.fries.reference.service.SignUrlServiceImpl;
import com.fr.fries.reference.sign.object.SignedUrl;
import com.fr.fries.reference.sign.request.SignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.fr.fries.reference.common.Constants.*;

@RestController
@RequiredArgsConstructor
@Validated
public class SignUrlController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SignUrlServiceImpl signUrlService;

    @PostMapping(value = URI_CREATE_UPLOAD_URL)
    public ResponseEntity<GetSignedUrlResponse> createUploadUrl(
            HttpServletRequest request,
            @RequestHeader(HEADER_USER_ID) @NotBlank(message = VALID_MSG_USER_ID_NOT_BLANK) @Size(
                    min = 1, max = 36, message = VALID_MSG_USER_ID_SIZE) String userId,
            @RequestHeader(value = HEADER_SU_CONTENT_TYPE) @NotBlank(
                    message = VALID_MSG_CONTENT_TYPE_NOT_BLANK) String contentType,
            @RequestHeader(value = HEADER_SU_CONTENT_LENGTH) @NotNull @Min(
                    value = 1, message = VALID_MSG_CONTENT_LENGTH_MIN) Long contentLength,
            @RequestParam @NotNull @Size(min = 10, max = 10, message = VALID_MSG_CID_SIZE) String cid)
            throws SignUrlException {
        log.debug("[CREATE UPLOAD URL] start controller");
        request.setAttribute(ATTR_API_NAME, API_V1_CREATE_UPLOAD_URL);
        request.setAttribute(ATTR_CONTENT_TYPE, contentType);
        request.setAttribute(ATTR_CONTENT_LENGTH, contentLength);

        SignedUrlRequest signedUrlReq = new SignedUrlRequest(userId, cid, contentType, contentLength);
        SignedUrl signedUrl = signUrlService.getUploadSignedUrl(signedUrlReq);
        request.setAttribute(ATTR_OBJECT_ID, signedUrlReq.getObjectId());

        return ResponseEntity.ok(
                new GetSignedUrlResponse(signedUrlReq.getObjectId(), signedUrl, signedUrlReq.getTtl().toDays()));
    }

    @GetMapping(value = URI_GET_DOWNLOAD_URL)
    public ResponseEntity<GetSignedUrlResponse> getDownloadUrl(
            HttpServletRequest request,
            @RequestHeader(HEADER_USER_ID) @NotBlank(message = VALID_MSG_USER_ID_NOT_BLANK) @Size(
                    min = 1, max = 36, message = VALID_MSG_USER_ID_SIZE) String userId,
            @RequestHeader(value = HEADER_RESPONSE_FILENAME, required = false) String resFilename,
            @RequestHeader(value = HEADER_RESPONSE_CONTENT_TYPE, required = false) String resContentType,
            @RequestParam @NotNull @Size(min = 10, max = 10, message = VALID_MSG_CID_SIZE) String cid,
            @PathVariable @NotNull @Size(min = 36, max = 36, message = VALID_MSG_OBJECT_ID_SIZE) String objectId)
            throws SignUrlException {
        log.debug("[GET DOWNLOAD URL] start controller");
        request.setAttribute(ATTR_API_NAME, API_V1_GET_DOWNLOAD_URL);
        request.setAttribute(ATTR_OBJECT_ID, objectId);
        request.setAttribute(ATTR_RES_FILENAME, resFilename);
        request.setAttribute(ATTR_RES_CONTENT_TYPE, resContentType);

        SignedUrlRequest signedUrlReq = new SignedUrlRequest(userId, cid, objectId, resFilename, resContentType);
        SignedUrl signedUrl = signUrlService.getDownloadSignedUrl(signedUrlReq);

        return ResponseEntity.ok(new GetSignedUrlResponse(signedUrlReq.getObjectId(), signedUrl));
    }

    @DeleteMapping(value = URI_DELETE_OBJECT)
    public ResponseEntity<ServletResponse> deleteObject(
            HttpServletRequest request,
            @RequestHeader(HEADER_USER_ID) @NotNull @Size(
                    min = 1, max = 36, message = VALID_MSG_USER_ID_SIZE) String userId,
            @RequestParam @NotNull @Size(min = 10, max = 10, message = VALID_MSG_CID_SIZE) String cid,
            @PathVariable @NotNull @Size(min = 36, max = 36, message = VALID_MSG_OBJECT_ID_SIZE) String objectId)
            throws SignUrlException {
        log.debug("[DELETE OBJECT] start controller");
        request.setAttribute(ATTR_API_NAME, API_V1_DELETE_OBJECT);
        request.setAttribute(ATTR_OBJECT_ID, objectId);

        signUrlService.deleteObject(userId, cid, objectId);

        return ResponseEntity.ok().build();
    }
}
