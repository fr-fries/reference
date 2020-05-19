package com.fr.fries.reference.api;

import com.fr.fries.reference.BaseTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

public class DeleteObjectTest extends BaseTest {

    @Test
    public void deleteObject_whenNormal_then200OK() throws Exception {
        v1DeleteObject(appId, userId, cid, objectId)
                .apply(document("deleteObject",
                        getDocumentRequest(),
                        getDocumentResponse()
                ))
                .statusCode(HttpStatus.OK.value());
    }
}
