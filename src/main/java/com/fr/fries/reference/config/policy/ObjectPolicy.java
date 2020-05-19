package com.fr.fries.reference.config.policy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectPolicy {

    private Duration ttl;
    private DataSize maxContentLength;
}
