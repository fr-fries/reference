package com.fr.fries.reference.config.policy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyInfo {

    private List<String> cids;
    private ObjectPolicy objectPolicy;
    private MakeUrlPolicy makeUrlPolicy;
}
