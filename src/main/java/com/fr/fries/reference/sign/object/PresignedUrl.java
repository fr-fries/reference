package com.fr.fries.reference.sign.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresignedUrl {

    String method;
    String url;
    long expires;
}
