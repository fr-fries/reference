package com.fr.fries.reference.config.policy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakeUrlPolicy {

    private UrlPolicy uploadUrl;
    private UrlPolicy downloadUrl;
}
