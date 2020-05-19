package com.fr.fries.reference.sign;

import com.fr.fries.reference.common.SignerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;

@Component
@RequiredArgsConstructor
public class SignerFactory {

    private final S3Signer s3Signer;
    private final CfSigner cfSigner;

    private EnumMap<SignerType, Signer> signers = new EnumMap<>(SignerType.class);

    @PostConstruct
    private void init() {
        signers.put(SignerType.S3, s3Signer);
        signers.put(SignerType.CF, cfSigner);
    }

    public Signer getSigner(SignerType signerType) {
        return signers.get(signerType);
    }
}
