package com.fr.fries.reference.config.policy;

import com.amazonaws.HttpMethod;
import com.fr.fries.reference.common.SignerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlPolicy {

    private HttpMethod method;
    private SignerType type;
    @NonNull
    private String domain;
    private Duration expires;

    public long getExpiresMillis(long millisToAdd) {
        return this.expires.plusMillis(millisToAdd).toMillis();
    }
}
