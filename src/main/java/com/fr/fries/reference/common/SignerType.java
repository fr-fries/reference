package com.fr.fries.reference.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SignerType {
    @JsonProperty
    S3,
    @JsonProperty
    CF;
}
