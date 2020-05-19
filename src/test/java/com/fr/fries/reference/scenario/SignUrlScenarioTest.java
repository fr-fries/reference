package com.fr.fries.reference.scenario;

import com.fr.fries.reference.BaseTest;
import com.fr.fries.reference.controller.response.GetSignedUrlResponse;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SignUrlScenarioTest extends BaseTest {

    @Test
    public void whenWholeScenario_thenSuccess() throws Exception {
        // 1. create upload url
        GetSignedUrlResponse uploadUrl = v1CreateUploadUrl(appId, userId, contentType, (long) body.length(), cid)
                .statusCode(HttpStatus.OK.value())
                .extract().response().as(GetSignedUrlResponse.class);

        // 2. upload to s3
        httpRequest(uploadUrl.getUploadInfo().getUrl(), contentType, body);

        // 3. get download url
        GetSignedUrlResponse downloadUrl = v1GetDownloadUrl(appId, userId, null, null, cid, uploadUrl.getObjectId(), false)
                .statusCode(HttpStatus.OK.value())
                .extract().response().as(GetSignedUrlResponse.class);

        // 4. download data
        String response = httpRequest(downloadUrl.getDownloadInfo().getUrl());

        // 5. verify
        assertThat(response, equalTo(body));
    }
}
