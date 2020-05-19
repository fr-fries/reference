package com.fr.fries.reference.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fr.fries.reference.sign.object.PresignedUrl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPreSignedUrlResponse {

    long rcode;
    String rmsg;
    List<PresignedUrl> presigned;

    public GetPreSignedUrlResponse(List<PresignedUrl> presigned) {
        this.presigned = presigned;
    }
}