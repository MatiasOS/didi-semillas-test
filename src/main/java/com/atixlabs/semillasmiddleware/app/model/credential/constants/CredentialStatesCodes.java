package com.atixlabs.semillasmiddleware.app.model.credential.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CredentialStatesCodes {
    //IMPORTANT!! This keys and values are important to the front. For ex. "Credential_Active" is the key to get the value. In front this key is in a constant to use the filters.
    //be careful on any change here.

    CREDENTIAL_ACTIVE("Vigente"),
    CREDENTIAL_REVOKE("Revocada"),
    PENDING_DIDI("Pendiente-didi"),
    DEFAULT_DIDI("Mora");


    private String code;

    CredentialStatesCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    static final Map<String, CredentialStatesCodes> codeMap = Arrays.stream(values()).collect(Collectors.toMap(CredentialStatesCodes::getCode, p->p));

    public static CredentialStatesCodes getEnumByStringValue(String codeString) {
        return codeMap.get(codeString);
    }

}
